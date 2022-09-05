package garden.ephemeral.rocket.spectra.recovery

import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.color.Illuminant
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.PhysicalConstants
import garden.ephemeral.rocket.spectra.SpectralShape
import garden.ephemeral.rocket.util.Cache
import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.buildImmutableDoubleArray
import garden.ephemeral.rocket.util.toImmutableDoubleArray
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
    private val rhoX: ImmutableDoubleArray,
    private val rhoY: ImmutableDoubleArray,
    private val rhoZ: ImmutableDoubleArray,
    private val scalingFactor: Double
) : RecoveryMethod {
    override fun recoverSpectrum(color: CieXyzColor): DoubleSpectrum {
        var values = rhoX * color.x + rhoY * color.y + rhoZ * color.z
        if (values.any { it < 0 }) {
            values = values.map { it.coerceAtLeast(0.0) }.toImmutableDoubleArray()
        }
        return DoubleSpectrum(shape, values * scalingFactor)
    }

    companion object {
        // Cache is shared for both types because they will never collide
        private val cache = Cache<CacheKey, Burns2020Method1>()

        /**
         * Gets a cached instance of the spectrum recovery utility for the emissive case.
         *
         * @param colorMatchingFunction the color matching function to use.
         * @param shape the desired spectral shape.
         */
        fun get(colorMatchingFunction: ColorMatchingFunction, shape: SpectralShape) =
            cache.get(CacheKey(colorMatchingFunction, null, shape)) { (cmf, _, shape) ->
                val cmfSpectrum = cmf.spectrum(shape)

                // Inverse of factor used when converting in the other direction.
                val factor = 1.0 / (PhysicalConstants.MaximumLuminousEfficacy * shape.step)

                createCommon(
                    shape,
                    cmfSpectrum.xValues,
                    cmfSpectrum.yValues,
                    cmfSpectrum.zValues,
                    factor
                )
            }

        /**
         * Gets a cached instance of the spectrum recovery utility for the reflectance case.
         *
         * @param colorMatchingFunction the color matching function to use.
         * @param illuminant the illuminant to use.
         * @param shape the desired spectral shape.
         */
        fun get(colorMatchingFunction: ColorMatchingFunction, illuminant: Illuminant, shape: SpectralShape) =
            cache.get(CacheKey(colorMatchingFunction, illuminant, shape)) { (cmf, illuminant, shape) ->
                val cmfSpectrum = cmf.spectrum(shape)
                val illuminantSpectrum = illuminant!!.spectrum(shape)

                // Inverse of factor used when converting in the other direction.
                // Similar to the case in the opposite direction, we don't need to divide and then re-multiply
                // by shape.step and can just do the one dot product.
                // This could be cleaned up further if we moved this calculation into the utility.
                val factor = illuminantSpectrum.values.dotProduct(cmfSpectrum.yValues)

                createCommon(
                    shape,
                    cmfSpectrum.xValues * illuminantSpectrum.values,
                    cmfSpectrum.yValues * illuminantSpectrum.values,
                    cmfSpectrum.zValues * illuminantSpectrum.values,
                    factor
                )
            }

        // Logic for emission and reflectance is quite similar, varying only in which vectors are
        // passed in for X, Y and Z - so the common logic is handled here.
        private fun createCommon(
            shape: SpectralShape,
            xValues: ImmutableDoubleArray,
            yValues: ImmutableDoubleArray,
            zValues: ImmutableDoubleArray,
            factor: Double
        ): Burns2020Method1 {
            val n = shape.size
            val d = buildDiagonalMatrix(n)

            val scalingFactor = 1.0 / yValues.sum()

            val awTransposed = Matrix(
                3,
                n,
                buildImmutableDoubleArray {
                    addAll(xValues * scalingFactor)
                    addAll(yValues * scalingFactor)
                    addAll(zValues * scalingFactor)
                }
            )
            val aw = awTransposed.transpose()
            val bInverse = packMatrix(n, d, aw, awTransposed)

            val b = bInverse.inverse

            val rhoX = extractRho(0, b, n) * factor
            val rhoY = extractRho(1, b, n) * factor
            val rhoZ = extractRho(2, b, n) * factor

            return Burns2020Method1(shape, rhoX, rhoY, rhoZ, scalingFactor)
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
            n,
            n,
            buildImmutableDoubleArray {
                for (row in 0 until n) {
                    for (col in 0 until n) {
                        add(
                            when {
                                row == 0 && col == 0 -> 2.0
                                row == n - 1 && col == n - 1 -> 2.0
                                row == col -> 4.0
                                abs(row - col) == 1 -> -2.0
                                else -> 0.0
                            }
                        )
                    }
                }
            }
        )

        // Pack matrix like this:
        // [ d    Aw ]
        // [ Aw'  0  ]
        private fun packMatrix(n: Int, d: Matrix, aw: Matrix, awT: Matrix) = Matrix(
            n + 3,
            n + 3,
            buildImmutableDoubleArray {
                for (row in 0 until n) {
                    for (col in 0 until n) {
                        add(d[row, col])
                    }
                    for (col in 0 until 3) {
                        add(aw[row, col])
                    }
                }
                for (row in 0 until 3) {
                    for (col in 0 until n) {
                        add(awT[row, col])
                    }
                    for (col in 0 until 3) {
                        add(0.0)
                    }
                }
            }
        )

        // Extracts the rho values from the block in the top right.
        private fun extractRho(offset: Int, b: Matrix, n: Int) = buildImmutableDoubleArray {
            for (row in 0 until n) {
                add(b[row, n + offset])
            }
        }

        private data class CacheKey(
            val colorMatchingFunction: ColorMatchingFunction,
            val illuminant: Illuminant?,
            val shape: SpectralShape
        )
    }
}
