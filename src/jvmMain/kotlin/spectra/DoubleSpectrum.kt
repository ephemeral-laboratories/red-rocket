package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.color.Illuminant
import garden.ephemeral.rocket.color.RgbColor
import garden.ephemeral.rocket.spectra.recovery.Burns2020Method1
import garden.ephemeral.rocket.util.stack
import garden.ephemeral.rocket.util.stackNCopies
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.toNDArray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
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
    val values: D1Array<Double>,
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

        val factor = PhysicalConstants.MaximumLuminousEfficacy * shape.step

        val x = values dot colorMatchingFunctionSpectrum.xValues
        val y = values dot colorMatchingFunctionSpectrum.yValues
        val z = values dot colorMatchingFunctionSpectrum.zValues

        val xyz = mk.ndarray(mk[x, y, z]) * factor

        return CieXyzColor(xyz)
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
        val cmfSpectrum = colorMatchingFunction.spectrum(shape)
        val plainCmf = mk.stack(
            arrays = listOf(cmfSpectrum.xValues, cmfSpectrum.yValues, cmfSpectrum.zValues),
            axis = 1,
        ).transpose(1, 0)

        val illuminantSpectrum = illuminant.spectrum(shape)
        val illuminatedCmf = plainCmf * mk.stackNCopies(array = illuminantSpectrum.values, copies = 3, axis = 0)
        val factor = 1.0 / illuminatedCmf[1].sum()

        val xyz = (illuminatedCmf dot values) * factor

        return CieXyzColor(xyz)
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
        return DoubleSpectralData(newShape.wavelengths.zip(values.toList())).createSpectrum(newShape)
    }

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
            val values = shape.wavelengths
                .map { w ->
                    val wavelength = w * 1E-9

                    // Planck's Law of radiation
                    // P_λ dλ = (c₁/λ⁵)/(exp(c₂/λT) - 1) dλ  (units: W m^-3)
                    // Note that c₁ includes a 2π term which takes care of what would normally be
                    // a unit of sr⁻¹
                    PhysicalConstants.RadiationC1 * wavelength.pow(-5) / (exp(PhysicalConstants.RadiationC2 / (wavelength * temperature)) - 1)
                }
                .toNDArray()

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
