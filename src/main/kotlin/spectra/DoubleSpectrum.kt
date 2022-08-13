package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.color.Illuminant
import garden.ephemeral.rocket.color.RgbColor
import garden.ephemeral.rocket.spectra.recovery.Burns2020Method1
import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.buildImmutableDoubleArray
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

    operator fun get(wavelength: Wavelength): Double = values[wavelength.index]

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

        val factor = PhysicalConstants.MaximumLuminousEfficacy * shape.step

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

        // k = 1 / (shape.step * illumCmfY.sum()), but then factor = k * shape.step,
        // so we can skip some operations here.

        val factor = 1.0 / illumCmfY.sum()

        val x = values.dotProduct(illumCmfX) * factor
        val y = values.dotProduct(illumCmfY) * factor
        val z = values.dotProduct(illumCmfZ) * factor

        return CieXyzColor(x, y, z)
    }

    fun toLinearRgbEmission(): RgbColor = toCieXyzEmission().toLinearRgb()
    fun toLinearRgbReflectance(): RgbColor = toCieXyzReflectance().toLinearRgb()

    companion object {

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
                    val wavelength = w.inMetres

                    // Planck's Law of radiation
                    // P_λ dλ = (c₁/λ⁵)/(exp(c₂/λT) - 1) dλ  (units: W m^-3)
                    // Note that c₁ includes a 2π term which takes care of what would normally be
                    // a unit of sr⁻¹
                    val p = PhysicalConstants.RadiationC1 * wavelength.pow(-5) / (exp(PhysicalConstants.RadiationC2 / (wavelength * temperature)) - 1)
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
            return Burns2020Method1.get(colorMatchingFunction, shape)
                .recoverSpectrum(color)
        }

        /**
         * Recovers a reflectance spectrum from a CIE XYZ color.
         *
         * This is essentially the inverse of [toCieXyzReflectance] and the units are the same.
         *
         * There is no single correct solution for this operation, so this implementation tries to
         * produce the result with the lowest possible norm, i.e. a least-squares solution.
         *
         * @param color the input color.
         * @param shape the shape you want for the resulting spectrum.
         * @param colorMatchingFunction the color matching function to use.
         * @param illuminant the illuminant to use.
         * @return the spectrum.
         */
        fun recoverFromCieXyzReflectance(
            color: CieXyzColor,
            shape: SpectralShape = SpectralShape.Default,
            colorMatchingFunction: ColorMatchingFunction = ColorMatchingFunction.CIE_1931_2_DEGREE,
            illuminant: Illuminant = Illuminant.D65
        ): DoubleSpectrum {
            return Burns2020Method1.get(colorMatchingFunction, illuminant, shape)
                .recoverSpectrum(color)
        }
    }
}
