package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.color.RgbColor
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

    fun toCieXyz(
        colorMatchingFunction: ColorMatchingFunction = ColorMatchingFunction.CIE_1931_2_DEGREE
    ): CieXyzColor {
        val colorMatchingFunctionSpectrum = colorMatchingFunction.spectrum(shape)

        val factor = Km * shape.step

        val x = values.dotProduct(colorMatchingFunctionSpectrum.xValues) * factor
        val y = values.dotProduct(colorMatchingFunctionSpectrum.yValues) * factor
        val z = values.dotProduct(colorMatchingFunctionSpectrum.zValues) * factor

        return CieXyzColor(x, y, z)
    }

    fun toLinearRgb(): RgbColor = toCieXyz().toLinearRgb()

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
            shape: SpectralShape = SpectralShape.Default,
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
    }
}