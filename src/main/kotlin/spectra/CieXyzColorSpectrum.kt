package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.util.ImmutableDoubleArray

/**
 * A spectrum of data at different wavelengths, where each value is a [CieXyzColor].
 *
 * @param shape the shape of the spectral data.
 * @param xValues the array of X values for the spectrum.
 * @param yValues the array of Y values for the spectrum.
 * @param zValues the array of Z values for the spectrum.
 */
class CieXyzColorSpectrum(
    shape: SpectralShape,
    var xValues: ImmutableDoubleArray,
    var yValues: ImmutableDoubleArray,
    var zValues: ImmutableDoubleArray
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
