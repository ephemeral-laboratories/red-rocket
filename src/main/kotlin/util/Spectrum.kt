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
 * @param shape the shape of the spectral data.
 * @param values the list of entries for the spectrum.
 * @param adapter an adapter class specifying how to manipulate values.
 */
class Spectrum<T>(
    var shape: SpectralShape,
    var values: List<T>,
    private var adapter: ValueAdapter<T>
) {
    val wavelengths get() = shape.wavelengths

    init {
        if (values.size != shape.size) {
            throw IllegalArgumentException("Values has wrong number of elements ($values.size) for shape $shape, " +
                    "expected ${shape.size}")
        }
    }

    operator fun plus(other: Spectrum<T>): Spectrum<T> {
        requireSameShape(other)
        return Spectrum(
            shape,
            values.zip(other.values).map { (x, y) -> adapter.add(x, y) },
            adapter
        )
    }

    operator fun times(other: Spectrum<T>): Spectrum<T> {
        requireSameShape(other)
        return Spectrum(
            shape,
            values.zip(other.values).map { (x, y) -> adapter.times(x, y) },
            adapter
        )
    }

    private fun requireSameShape(other: Spectrum<*>) {
        if (other.shape != shape) {
            throw IllegalArgumentException("Other spectrum's shape (${other.shape}) does not match ours ($shape)")
        }
    }

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
        ): Spectrum<Double> {
            val values = buildList {
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
            return Spectrum(shape, values, ValueAdapter.forDouble)
        }

        /**
         * Divides this spectrum by another spectrum.
         *
         * @receiver a spectrum.
         * @param other the other spectrum.
         * @return the result of element-wise division by the other spectrum.
         */
        operator fun Spectrum<Double>.div(other: Spectrum<Double>): Spectrum<Double> {
            requireSameShape(other)
            return Spectrum(
                shape,
                values.zip(other.values).map { (x, y) -> x / y },
                adapter
            )
        }

        fun Spectrum<Double>.toCieXyz(
            colorMatchingFunction: ColorMatchingFunction = ColorMatchingFunction.CIE_1931_2_DEGREE
        ): CieXyzColor {
            var total = CieXyzColor(0.0, 0.0, 0.0)

            val colorMatchingFunctionValues = colorMatchingFunction.spectrum(shape).values

            for (index in 0 until shape.size) {
                val intensity = values[index]
                val colorMatch = colorMatchingFunctionValues[index]
                total += colorMatch * intensity
            }

            return total * Km * shape.step
        }

        fun Spectrum<Double>.toLinearRgb(): RgbColor {
            return toCieXyz().toLinearRgb()
        }
    }

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
