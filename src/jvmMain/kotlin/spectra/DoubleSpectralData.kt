package garden.ephemeral.rocket.spectra

import org.jetbrains.kotlinx.multik.api.toNDArray

/**
 * Holder for spectral data of type double.
 *
 * For performance reasons, spectral data is not used directly, but converted to
 * a `Spectrum` for actual use, so that all spectra which are used for computation
 * are the same shape.
 */
class DoubleSpectralData(val data: List<Pair<Double, Double>>) : SpectralData<DoubleSpectrum>() {
    init {
        require(data.size >= 2) { "Must be at least two data points! Got: $data" }
        for (i in 0 until data.lastIndex) {
            require(data[i].first < data[i + 1].first) { "Data points must be sorted! Got: $data" }
        }
    }

    override fun createSpectrum(shape: SpectralShape): DoubleSpectrum {
        var dataIndex = 0
        val values = shape.wavelengths
            .map { w ->
                when {
                    w == data[dataIndex].first -> data[dataIndex].second

                    w < data[dataIndex].first -> {
                        // Low extrapolation case handled here by coercing to >= 1
                        interpolate(w, dataIndex.coerceAtLeast(1))
                    }

                    else -> {
                        // w > data[dataIndex].first
                        // Move data pointer until we either find a value which is higher, _or_ we hit the end.
                        // Takes care of the high extrapolation case automatically.
                        while (w > data[dataIndex].first && dataIndex < data.lastIndex) {
                            dataIndex++
                        }
                        interpolate(w, dataIndex)
                    }
                }
            }
            .toNDArray()

        return DoubleSpectrum(shape, values)
    }

    private fun interpolate(wavelength: Double, upperIndex: Int): Double {
        val wavelength1 = data[upperIndex - 1].first
        val value1 = data[upperIndex - 1].second
        val wavelength2 = data[upperIndex].first
        val value2 = data[upperIndex].second

        // Compute slope between the two points
        val dy = value2 - value1
        val dx = wavelength2 - wavelength1
        val m = dy / dx

        return value1 + m * (wavelength - wavelength1)
    }
}
