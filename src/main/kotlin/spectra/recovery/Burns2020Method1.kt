package garden.ephemeral.rocket.spectra.recovery

import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.SpectralShape
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
 * This implementation does not take the illuminant into account at all
 * as it's currently only used for the emissive case.
 * Accounting for the illuminant requires adjusting the logic slightly,
 * which introduces a new scaling factor that I haven't yet figured out how to
 * compensate for.
 *
 * The method is relatively fast, requiring only 3 vector-scalar multiplies
 * to compute a spectrum for a single color. The rest of the required calculations
 * are cached between invocations.
 */
class Burns2020Method1 private constructor(
    colorMatchingFunction: ColorMatchingFunction,
    private val shape: SpectralShape
) {
    private val rhoX: ImmutableDoubleArray
    private val rhoY: ImmutableDoubleArray
    private val rhoZ: ImmutableDoubleArray
    private val scalingFactor: Double

    init {
        val colorMatchingFunctionSpectrum = colorMatchingFunction.spectrum(shape)

        val n = shape.size

        // Build a diagonal matrix like this:
        // [  2 -2  0 ...  0  0  0 ]
        // [ -2  4 -2 ...  0  0  0 ]
        // [  0 -2  4 ...  0  0  0 ]
        // [    ...          ...   ]
        // [  0  0  0 ...  4 -2  0 ]
        // [  0  0  0 ... -2  4 -2 ]
        // [  0  0  0 ...  0 -2  2 ]
        val d = Matrix(n, n, buildImmutableDoubleArray {
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
        })

        scalingFactor = 1.0 / colorMatchingFunctionSpectrum.yValues.sum()

        val awTransposed = Matrix(3, n, buildImmutableDoubleArray {
            addAll(colorMatchingFunctionSpectrum.xValues * scalingFactor)
            addAll(colorMatchingFunctionSpectrum.yValues * scalingFactor)
            addAll(colorMatchingFunctionSpectrum.zValues * scalingFactor)
        })
        val aw = awTransposed.transpose()

        // Pack matrix like this:
        // [ d    Aw ]
        // [ Aw'  0  ]
        val bInverse = Matrix(n + 3, n + 3, buildImmutableDoubleArray {
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
                    add(awTransposed[row, col])
                }
                for (col in 0 until 3) {
                    add(0.0)
                }
            }
        })

        val b = bInverse.inverse

        // Extract the rho values from the block in the top right.
        fun extractRho(offset: Int) = buildImmutableDoubleArray {
            for (row in 0 until n) {
                add(b[row, n + offset])
            }
        }

        rhoX = extractRho(0)
        rhoY = extractRho(1)
        rhoZ = extractRho(2)
    }

    /**
     * Recovers a spectrum for an XYZ color.
     *
     * @param color the color.
     * @return the recovered spectrum.
     */
    fun recoverSpectrum(color: CieXyzColor): DoubleSpectrum {
        var values = rhoX * color.x + rhoY * color.y + rhoZ * color.z
        if (values.any { it < 0 }) {
            values = values.map { it.coerceAtLeast(0.0) }.toImmutableDoubleArray()
        }
        return DoubleSpectrum(shape, values * scalingFactor)
    }

    companion object {
        private val cache = linkedMapOf<CacheKey, Burns2020Method1>()

        /**
         * Gets a cached instance of the spectrum recovery utility.
         *
         * @param colorMatchingFunction the color matching function to use.
         * @param shape the desired spectral shape.
         */
        fun get(colorMatchingFunction: ColorMatchingFunction, shape: SpectralShape) =
            cache.computeIfAbsent(CacheKey(colorMatchingFunction, shape)) { (cmf, shape) ->
                Burns2020Method1(cmf, shape)
            }

        private data class CacheKey(val colorMatchingFunction: ColorMatchingFunction, val shape: SpectralShape)
    }
}
