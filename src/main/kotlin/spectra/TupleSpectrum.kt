package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.util.ImmutableDoubleArray

/**
 * A spectrum of data at different wavelengths, where each value is a [Tuple].
 *
 * @param shape the shape of the spectral data.
 * @param xValues the array of X values for the spectrum.
 * @param yValues the array of Y values for the spectrum.
 * @param zValues the array of Z values for the spectrum.
 * @param wValues the array of W values for the spectrum.
 */
class TupleSpectrum(
    shape: SpectralShape,
    val xValues: ImmutableDoubleArray,
    val yValues: ImmutableDoubleArray,
    val zValues: ImmutableDoubleArray,
    val wValues: ImmutableDoubleArray
) : Spectrum<Tuple, TupleSpectrum>(shape) {
    init {
        requireSameSize("xValues", xValues)
        requireSameSize("yValues", yValues)
        requireSameSize("zValues", zValues)
        requireSameSize("wValues", wValues)
    }

    override fun plus(other: TupleSpectrum): TupleSpectrum {
        return TupleSpectrum(
            shape,
            xValues + other.xValues,
            yValues + other.yValues,
            zValues + other.zValues,
            wValues + other.wValues
        )
    }

    override fun times(other: TupleSpectrum): TupleSpectrum {
        return TupleSpectrum(
            shape,
            xValues * other.xValues,
            yValues * other.yValues,
            zValues * other.zValues,
            wValues * other.wValues
        )
    }
}