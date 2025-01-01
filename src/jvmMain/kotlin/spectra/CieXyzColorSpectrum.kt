package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.color.CieXyzColor
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times

/**
 * A spectrum of data at different wavelengths, where each value is a [CieXyzColor].
 *
 * @param shape the shape of the spectral data.
 * @property xValues the array of X values for the spectrum.
 * @property yValues the array of Y values for the spectrum.
 * @property zValues the array of Z values for the spectrum.
 */
class CieXyzColorSpectrum(
    shape: SpectralShape,
    val xValues: D1Array<Double>,
    val yValues: D1Array<Double>,
    val zValues: D1Array<Double>,
) : Spectrum<CieXyzColor, CieXyzColorSpectrum>(shape) {
    init {
        requireSameSize("xValues", xValues)
        requireSameSize("yValues", yValues)
        requireSameSize("zValues", zValues)
    }

    override fun plus(other: CieXyzColorSpectrum): CieXyzColorSpectrum {
        requireSameShape(other)
        return CieXyzColorSpectrum(shape, xValues + other.xValues, yValues + other.yValues, zValues + other.zValues)
    }

    override fun times(other: CieXyzColorSpectrum): CieXyzColorSpectrum {
        requireSameShape(other)
        return CieXyzColorSpectrum(shape, xValues * other.xValues, yValues * other.yValues, zValues * other.zValues)
    }
}
