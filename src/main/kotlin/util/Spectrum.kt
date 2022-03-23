package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorMatchingFunction
import garden.ephemeral.rocket.color.RgbColor
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow

/**
 * A spectrum of data at different wavelengths, allowing getting values back out,
 * or computing (interpolating and extrapolating) values which are between the
 * known values.
 *
 * @param data the list of entries for the spectrum.
 * @param adapter an adapter class specifying how to manipulate values.
 */
class Spectrum<T>(
    data: List<SpectrumEntry<T>>,
    private var adapter: ValueAdapter<T>
) {
    val wavelengths = data.map { entry -> entry.wavelength }
    val values = data.map { entry -> entry.value }

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
        for (i in wavelengths.indices) {
            val currentWavelength = wavelengths[i]
            if (currentWavelength == wavelength) {
                return values[i]
            }

            if (currentWavelength > wavelength) {
                // Coercing to >= 1 handles the lower extrapolation case for free
                return interpolate(wavelength, i.coerceAtLeast(1))
            }
        }

        return interpolate(wavelength, wavelengths.lastIndex)
    }

    private fun interpolate(wavelength: Double, upperIndex: Int): T {
        val wavelength1 = wavelengths[upperIndex - 1]
        val value1 = values[upperIndex - 1]
        val wavelength2 = wavelengths[upperIndex]
        val value2 = values[upperIndex]

        // Compute slope between the two points
        val dy = adapter.sub(value2, value1)
        val dx = wavelength2 - wavelength1
        val m = adapter.times(dy, 1.0 / dx)

        return adapter.add(value1, adapter.times(m, wavelength - wavelength1))
    }

    operator fun plus(other: Spectrum<T>): Spectrum<T> = Spectrum(
        (wavelengths.asSequence() + other.wavelengths.asSequence())
            .sorted().distinct()
            .map { wavelength -> SpectrumEntry(wavelength, adapter.add(this[wavelength], other[wavelength])) }
            .toList(),
        adapter
    )

    operator fun times(other: Spectrum<T>): Spectrum<T> = Spectrum(
        (wavelengths.asSequence() + other.wavelengths.asSequence())
            .sorted().distinct()
            .map { wavelength -> SpectrumEntry(wavelength, adapter.times(this[wavelength], other[wavelength])) }
            .toList(),
        adapter
    )

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

        /**
         * Constructs a spectrum for black body radiation.
         *
         * @param temperature the temperature, in Kelvin.
         * @return the spectrum.
         */
        fun forBlackBodyRadiation(temperature: Double): Spectrum<Double> {
            val entries = buildList {
                for (w in 380..780 step 5) {
                    val wavelength = w * 1E-9

                    // Planck's Law of radiation
                    // P_λ dλ = (c₁/λ⁵)/(exp(c₂/λT) - 1) dλ  (units: W m^-3)
                    // Note that c₁ includes a 2π term which takes care of what would normally be
                    // a unit of sr⁻¹
                    val p = c1 * wavelength.pow(-5) / (exp(c2 / (wavelength * temperature)) - 1)
                    add(SpectrumEntry(w.toDouble(), p))
                }
            }
            return Spectrum(entries, ValueAdapter.forDouble)
        }

        fun Spectrum<Double>.toCieXyz(): CieXyzColor {
            var total = CieXyzColor(0.0, 0.0, 0.0)

            val colorMatchingFunction = ColorMatchingFunction.CIE_1931_2_DEGREE.spectrum
            for (w in colorMatchingFunction.wavelengths) {
                val intensity = this[w]
                val colorMatch = colorMatchingFunction[w]
                total += colorMatch * intensity
            }

            val scale = 1.0 / (total.x + total.y + total.z)
            return total * scale
        }

        fun Spectrum<Double>.toLinearRgb(): RgbColor {
            return toCieXyz().toLinearRgb()
        }
    }

    data class SpectrumEntry<T>(
        val wavelength: Double,
        val value: T
    )

    interface ValueAdapter<T> {
        fun add(x: T, y: T): T
        fun sub(x: T, y: T): T
        fun times(x: T, y: Double): T
        fun times(x: T, y: T): T

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
                override fun times(x: Tuple, y: Tuple): Tuple = x * y
            }

            val forCieXyzColor = object : ValueAdapter<CieXyzColor> {
                override fun add(x: CieXyzColor, y: CieXyzColor): CieXyzColor = x + y
                override fun sub(x: CieXyzColor, y: CieXyzColor): CieXyzColor = x - y
                override fun times(x: CieXyzColor, y: Double): CieXyzColor = x * y
                override fun times(x: CieXyzColor, y: CieXyzColor): CieXyzColor = x * y
            }
        }
    }
}
