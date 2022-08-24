package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.color.Illuminant
import garden.ephemeral.rocket.color.RgbColor
import garden.ephemeral.rocket.spectra.recovery.Burns2020Method1
import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.buildImmutableDoubleArray
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow

/**
 * A spectrum of data at different wavelengths, where each value is a `Double`.
 *
 * @param shape the shape of the spectral data.
 * @param values the array of entries for the spectrum.
 */
class DoubleSpectrum(
    shape: SpectralShape,
    val values: ImmutableDoubleArray
) : Spectrum<Double, DoubleSpectrum>(shape) {
    init {
        requireSameSize("values", values)
    }

    override fun plus(other: DoubleSpectrum): DoubleSpectrum {
        requireSameShape(other)
        return DoubleSpectrum(shape, values + other.values)
    }

    override fun times(other: DoubleSpectrum): DoubleSpectrum {
        requireSameShape(other)
        return DoubleSpectrum(shape, values * other.values)
    }

    operator fun div(other: DoubleSpectrum): DoubleSpectrum {
        requireSameShape(other)
        return DoubleSpectrum(shape, values / other.values)
    }

    /**
     * Converts a spectrum of emission values to a CIE XYZ color.
     *
     * The spectrum is taken to be in units of W sr⁻¹ m⁻² nm⁻¹.
     * The Y value of the resulting color (luminance) is thus in units of lm sr⁻¹ m⁻², or cd m⁻², or nit.
     *
     * @param colorMatchingFunction the color matching function to use.
     * @return the color.
     */
    fun toCieXyzEmission(
        colorMatchingFunction: ColorMatchingFunction = ColorMatchingFunction.CIE_1931_2_DEGREE
    ): CieXyzColor {
        val colorMatchingFunctionSpectrum = colorMatchingFunction.spectrum(shape)

        val factor = Km * shape.step

        val x = values.dotProduct(colorMatchingFunctionSpectrum.xValues) * factor
        val y = values.dotProduct(colorMatchingFunctionSpectrum.yValues) * factor
        val z = values.dotProduct(colorMatchingFunctionSpectrum.zValues) * factor

        return CieXyzColor(x, y, z)
    }

    /**
     * Converts a spectrum of reflectance values to a CIE XYZ color.
     *
     * The spectrum is taken to be in relative reflectance values from 0..1.
     *
     * @param colorMatchingFunction the color matching function to use.
     * @param illuminant the illuminant to use.
     * @return the color.
     */
    fun toCieXyzReflectance(
        colorMatchingFunction: ColorMatchingFunction = ColorMatchingFunction.CIE_1931_2_DEGREE,
        illuminant: Illuminant = Illuminant.D65
    ): CieXyzColor {
        val colorMatchingFunctionSpectrum = colorMatchingFunction.spectrum(shape)
        val illuminantSpectrum = illuminant.spectrum(shape)

        val illumCmfX = illuminantSpectrum.values * colorMatchingFunctionSpectrum.xValues
        val illumCmfY = illuminantSpectrum.values * colorMatchingFunctionSpectrum.yValues
        val illumCmfZ = illuminantSpectrum.values * colorMatchingFunctionSpectrum.zValues

        // XXX: The logic divides by shape.step but later multiplies by it, so there is probably a shortcut

        val k = 1.0 / (shape.step * illumCmfY.sum())
        val factor = k * shape.step

        val x = values.dotProduct(illumCmfX) * factor
        val y = values.dotProduct(illumCmfY) * factor
        val z = values.dotProduct(illumCmfZ) * factor

        return CieXyzColor(x, y, z)
    }

    fun toLinearRgbEmission(): RgbColor = toCieXyzEmission().toLinearRgb()
    fun toLinearRgbReflectance(): RgbColor = toCieXyzReflectance().toLinearRgb()

    /**
     * Reshapes the spectrum to a different shape.
     *
     * Depending on the original shape and the desired shape, this can be a somewhat
     * costly operation due to interpolation calculations, which you don't want to
     * be doing frequently, thus most operations between two spectra do not reshape
     * the spectra automatically.
     *
     * @param newShape the shape of the new spectrum.
     * @return the spectrum in that shape.
     */
    fun reshape(newShape: SpectralShape): DoubleSpectrum {
        return DoubleSpectralData(newShape.wavelengths.zip(values)).createSpectrum(newShape)
    }

    companion object {
        // Planck's constant (units: m² kg s⁻¹)
        private const val h: Double = 6.62607004E-34

        // Speed of light in a vacuum (units: m s⁻¹)
        private const val c: Double = 299_792_458.0

        // Boltzmann constant (units: m² kg s⁻² K⁻¹)
        private const val k: Double = 1.38064852E-23

        // Radiation constant c₁ = 2π h c² (units: m⁴ kg s⁻³ = W m²)
        private const val c1: Double = 2 * PI * h * c * c

        // Radiation constant c₂ = h c / k (units: m K)
        private const val c2: Double = h * c / k

        // Maximum luminous efficacy constant (units lm W⁻¹)
        private const val Km = 683.002

        /**
         * Constructs a spectrum for black body radiation.
         *
         * @param temperature the temperature, in Kelvin.
         * @return the spectrum.
         */
        fun forBlackBodyRadiation(
            temperature: Double,
            shape: SpectralShape = SpectralShape.Default
        ): DoubleSpectrum {
            val values = buildImmutableDoubleArray {
                shape.wavelengths.forEach { w ->
                    val wavelength = w * 1E-9

                    // Planck's Law of radiation
                    // P_λ dλ = (c₁/λ⁵)/(exp(c₂/λT) - 1) dλ  (units: W m^-3)
                    // Note that c₁ includes a 2π term which takes care of what would normally be
                    // a unit of sr⁻¹
                    val p = c1 * wavelength.pow(-5) / (exp(c2 / (wavelength * temperature)) - 1)
                    add(p)
                }
            }
            return DoubleSpectrum(shape, values)
        }

        /**
         * Recovers an emission spectrum from a CIE XYZ color.
         *
         * This is essentially the inverse of [toCieXyzEmission] and the units are the same.
         *
         * There is no single correct solution for this operation, so this implementation tries to
         * produce the result with the lowest possible norm, i.e. a least-squares solution.
         *
         * @param color the input color.
         * @param shape the shape you want for the resulting spectrum.
         * @param colorMatchingFunction the color matching function to use.
         * @return the spectrum.
         */
        fun recoverFromCieXyzEmission(
            color: CieXyzColor,
            shape: SpectralShape = SpectralShape.Default,
            colorMatchingFunction: ColorMatchingFunction = ColorMatchingFunction.CIE_1931_2_DEGREE
        ): DoubleSpectrum {
            // Inverse of factor used when converting in the other direction.
            val factor = 1.0 / (Km * shape.step)

            return Burns2020Method1.get(colorMatchingFunction, shape)
                .recoverSpectrum(color * factor)
        }
    }
}
