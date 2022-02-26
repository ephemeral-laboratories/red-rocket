package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Tuple

/**
 * A spectrum of data at different wavelengths, allowing getting values back out,
 * or computing (interpolating and extrapolating) values which are between the
 * known values.
 *
 * @param data the pair-wise data, each pair being wavelength and value.
 * @param adapter an adapter class specifying how to manipulate values.
 */
class Spectrum<T>(
    private var data: List<SpectrumEntry<T>>,
    private var adapter: ValueAdapter<T>
) {
    private val wavelengths get() = data.map { entry -> entry.wavelength }

    init {
        if (data.size < 2) {
            throw IllegalArgumentException("Must be at least two data points! Got: $data")
        }

        for (i in 0 until data.lastIndex) {
            if (data[i].wavelength >= data[i + 1].wavelength) {
                throw IllegalArgumentException("Data points must be sorted! Got: $data")
            }
        }
    }

    operator fun get(wavelength: Double): T {
        var lowerEntry: SpectrumEntry<T>? = null
        for (i in data.indices) {
            val entry = data[i]
            val currentWavelength = entry.wavelength
            val currentValue = entry.value
            if (currentWavelength == wavelength) {
                return currentValue
            }

            if (currentWavelength > wavelength) {
                return if (lowerEntry == null) {
                    interpolate(wavelength, entry, data[1])
                } else {
                    interpolate(wavelength, lowerEntry, entry)
                }
            }

            lowerEntry = entry
        }

        return interpolate(wavelength, data[data.size - 2], data[data.size - 1])
    }

    private fun interpolate(wavelength: Double, entry1: SpectrumEntry<T>, entry2: SpectrumEntry<T>): T {
        // Compute slope between the two points
        val dy = adapter.sub(entry2.value, entry1.value)
        val dx = entry2.wavelength - entry1.wavelength
        val m = adapter.times(dy, 1.0 / dx)

        return adapter.add(entry1.value, adapter.times(m, wavelength - entry1.wavelength))
    }

    operator fun plus(other: Spectrum<T>): Spectrum<T> = Spectrum(
        (wavelengths.asSequence() + other.wavelengths.asSequence())
            .sorted().distinct()
            .map { wavelength -> SpectrumEntry(wavelength, adapter.add(this[wavelength], other[wavelength])) }
            .toList(),
        adapter
    )

    data class SpectrumEntry<T>(
        val wavelength: Double,
        val value: T
    )

    interface ValueAdapter<T> {
        fun add(x: T, y: T): T
        fun sub(x: T, y: T): T
        fun times(x: T, y: Double): T

        companion object {
            val forDouble = object : ValueAdapter<Double> {
                override fun add(x: Double, y: Double): Double = x + y
                override fun sub(x: Double, y: Double): Double = x - y
                override fun times(x: Double, y: Double): Double = x * y
            }

            val forTuple = object : ValueAdapter<Tuple> {
                override fun add(x: Tuple, y: Tuple): Tuple = x + y
                override fun sub(x: Tuple, y: Tuple): Tuple = x - y
                override fun times(x: Tuple, y: Double): Tuple = x * y
            }
        }
    }
}