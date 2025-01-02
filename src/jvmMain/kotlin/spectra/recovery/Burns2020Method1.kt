package garden.ephemeral.rocket.spectra.recovery

import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.color.Illuminant
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.PhysicalConstants
import garden.ephemeral.rocket.spectra.SpectralShape
import garden.ephemeral.rocket.util.stackNCopies
import org.jetbrains.kotlinx.multik.api.d2arrayIndices
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.clip
import org.jetbrains.kotlinx.multik.ndarray.operations.stack
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.abs

/**
 * Implements method 1 for reflectance recovery as described in:
 *
 * _Burns, SA. Numerical methods for smoothest reflectance reconstruction.
 * Color Res Appl. 2020; 45: 8–21._ https://doi.org/10.1002/col.22437
 *
 * This is used for the emissive case (where you have no illuminant) as well
 * as the reflectance case (where you need an illuminant).
 *
 * The method is relatively fast, requiring only 3 vector-scalar multiplies
 * to compute a spectrum for a single color. The rest of the required calculations
 * are cached between invocations.
 */
class Burns2020Method1 private constructor(
    private val shape: SpectralShape,
    private val rhoXYZ: D2Array<Double>,
    private val scalingFactor: Double
) : RecoveryMethod {
    override fun recoverSpectrum(color: CieXyzColor): DoubleSpectrum {
        val values = (rhoXYZ dot color.xyz).clip(min = 0.0, max = Double.POSITIVE_INFINITY)
        return DoubleSpectrum(shape, values * scalingFactor)
    }

    private sealed interface CacheKey {
        data class WithIlluminant(
            val colorMatchingFunction: ColorMatchingFunction,
            val illuminant: Illuminant,
            val shape: SpectralShape,
        ) : CacheKey

        data class WithoutIlluminant(
            val colorMatchingFunction: ColorMatchingFunction,
            val shape: SpectralShape,
        ) : CacheKey
    }

    companion object {

        // Cache is shared for both types because they will never collide
        private val cache = linkedMapOf<CacheKey, Burns2020Method1>()
        private inline fun <reified K : CacheKey> cacheLookup(key: K, crossinline factory: (K) -> Burns2020Method1) =
            cache.computeIfAbsent(key) { factory(it as K) }

        /**
         * Gets a cached instance of the spectrum recovery utility for the emissive case.
         *
         * @param colorMatchingFunction the color matching function to use.
         * @param shape the desired spectral shape.
         */
        fun get(colorMatchingFunction: ColorMatchingFunction, shape: SpectralShape) =
            cacheLookup(CacheKey.WithoutIlluminant(colorMatchingFunction, shape)) { (cmf, shape) ->
                val cmfSpectrum = cmf.spectrum(shape)
                val plainCmf = mk.stack(
                    arrays = listOf(cmfSpectrum.xValues, cmfSpectrum.yValues, cmfSpectrum.zValues),
                    axis = 1,
                ).transpose(1, 0)

                // Inverse of factor used when converting in the other direction.
                val factor = 1.0 / (PhysicalConstants.MaximumLuminousEfficacy * shape.step)

                createCommon(shape, plainCmf, factor)
            }

        /**
         * Gets a cached instance of the spectrum recovery utility for the reflectance case.
         *
         * @param colorMatchingFunction the color matching function to use.
         * @param illuminant the illuminant to use.
         * @param shape the desired spectral shape.
         */
        fun get(colorMatchingFunction: ColorMatchingFunction, illuminant: Illuminant, shape: SpectralShape) =
            cacheLookup(CacheKey.WithIlluminant(colorMatchingFunction, illuminant, shape)) { (cmf, illuminant, shape) ->
                val cmfSpectrum = cmf.spectrum(shape)
                val plainCmf = mk.stack(
                    arrays = listOf(cmfSpectrum.xValues, cmfSpectrum.yValues, cmfSpectrum.zValues),
                    axis = 1,
                ).transpose(1, 0)

                val illuminantSpectrum = illuminant.spectrum(shape)
                val illuminatedCmf = plainCmf * mk.stackNCopies(array = illuminantSpectrum.values, copies = 3, axis = 0)

                // Inverse of factor used when converting in the other direction.
                val factor = illuminatedCmf[1].sum()

                createCommon(shape, illuminatedCmf, factor)
            }

        // Logic for emission and reflectance is quite similar, varying only in which vectors are
        // passed in for X, Y and Z - so the common logic is handled here.
        private fun createCommon(
            shape: SpectralShape,
            xyzValues: D2Array<Double>,
            factor: Double
        ): Burns2020Method1 {
            require(xyzValues.shape[0] == 3) { "wrong matrix orientation?" }

            val n = shape.size
            val d = buildDiagonalMatrix(n)

            val scalingFactor = 1.0 / xyzValues[1].sum()

            val awTransposed = Matrix(xyzValues * scalingFactor)
            val aw = awTransposed.transpose()
            val bInverse = packMatrix(n, d, aw, awTransposed)

            val b = bInverse.inverse

            val rhoXYZ = extractRho2D(b, n) * factor

            return Burns2020Method1(shape, rhoXYZ, scalingFactor)
        }

        // Build a diagonal matrix like this:
        // [  2 -2  0 ...  0  0  0 ]
        // [ -2  4 -2 ...  0  0  0 ]
        // [  0 -2  4 ...  0  0  0 ]
        // [    ...          ...   ]
        // [  0  0  0 ...  4 -2  0 ]
        // [  0  0  0 ... -2  4 -2 ]
        // [  0  0  0 ...  0 -2  2 ]
        private fun buildDiagonalMatrix(n: Int) = Matrix(
            mk.d2arrayIndices(n, n) { col, row ->
                when {
                    row == 0 && col == 0 -> 2.0
                    row == n - 1 && col == n - 1 -> 2.0
                    row == col -> 4.0
                    abs(row - col) == 1 -> -2.0
                    else -> 0.0
                }
            }
        )

        // Pack matrix like this:
        // [ d    Aw ]
        // [ Aw'  0  ]
        private fun packMatrix(n: Int, d: Matrix, aw: Matrix, awT: Matrix) = Matrix(
            d.cells.cat(aw.cells, axis = 1).cat(
                awT.cells.cat(mk.zeros(3, 3), axis = 1),
            )
        )

        // Extracts the rho values from the block in the top right.
        private fun extractRho2D(b: Matrix, n: Int): MultiArray<Double, D2> {
            return b.cells[(0..<n), (n..<n+3)]
        }
    }
}
