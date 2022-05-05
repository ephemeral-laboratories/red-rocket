package garden.ephemeral.rocket.util

/**
 * Holder for spectral data.
 *
 * For performance reasons, spectral data is not used directly, but converted to
 * a `Spectrum` for actual use, so that all spectra which are used for computation
 * are the same shape.
 */
class SpectralData<T>(val data: List<Pair<Double, T>>, private val adapter: Spectrum.ValueAdapter<T>) {
    init {
        require(data.size >= 2) { "Must be at least two data points! Got: $data" }
        for (i in 0 until data.lastIndex) {
            require(data[i].first < data[i + 1].first) { "Data points must be sorted! Got: $data" }
        }
    }

    /**
     * Creates a spectrum from the data.
     * Interpolates the data as needed to fit the given shape.
     *
     * @param shape the desired shape.
     * @return the spectrum.
     */
    fun createSpectrum(shape: SpectralShape = SpectralShape.Default): Spectrum<T> {
        var dataIndex = 0
        val values = mutableListOf<T>()
        for (w in shape.wavelengths) {
            when {
                w == data[dataIndex].first -> values.add(data[dataIndex].second)
                w < data[dataIndex].first -> {
                    // Low extrapolation case handled here by coercing to >= 1
                    values.add(interpolate(w, dataIndex.coerceAtLeast(1)))
                }
                else -> { // w > data[dataIndex].first
                    // Move data pointer until we either find a value which is higher, _or_ we hit the end.
                    // Takes care of the high extrapolation case automatically.
                    while (w > data[dataIndex].first && dataIndex < data.lastIndex) {
                        dataIndex++
                    }
                    values.add(interpolate(w, dataIndex))
                }
            }
        }
        return Spectrum(shape, values, adapter)
    }

    private fun interpolate(wavelength: Double, upperIndex: Int): T {
        val wavelength1 = data[upperIndex - 1].first
        val value1 = data[upperIndex - 1].second
        val wavelength2 = data[upperIndex].first
        val value2 = data[upperIndex].second

        // Compute slope between the two points
        val dy = adapter.sub(value2, value1)
        val dx = wavelength2 - wavelength1
        val m = adapter.times(dy, 1.0 / dx)

        return adapter.add(value1, adapter.times(m, wavelength - wavelength1))
    }
}