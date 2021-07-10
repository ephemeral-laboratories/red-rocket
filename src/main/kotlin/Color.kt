package garden.ephemeral.rocket

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

data class Color(val r: Double, val g: Double, val b: Double) {
    val isBlack:     Boolean get() = r == 0.0 && g == 0.0 && b == 0.0
    val isNonBlack:  Boolean get() = !isBlack

    companion object {
        val black = Color(0.0, 0.0, 0.0)
        val white = Color(1.0, 1.0, 1.0)

        fun grey(v: Double): Color { return Color(v, v, v) }

        fun fromSRgbDoubles(r: Double, g: Double, b: Double): Color {
            fun convert(v: Double): Double {
                return if (v < 0.04045) {
                    v / 12.92
                } else {
                    ((v + 0.055) / 1.055).pow(2.4)
                }
            }
            return Color(convert(r), convert(g), convert(b))
        }

        fun fromSRgbInts(r: Int, g: Int, b: Int, scale: Int = 255): Color {
            fun convert(v: Int): Double {
                return v / scale.toDouble()
            }
           return fromSRgbDoubles(convert(r), convert(g), convert(b))
        }
    }

    operator fun plus(their: Color): Color {
        return Color(r + their.r, g + their.g, b + their.b)
    }

    operator fun minus(their: Color): Color {
        return Color(r - their.r, g - their.g, b - their.b)
    }

    operator fun times(scalar: Double): Color {
        return Color(r * scalar, g * scalar, b * scalar)
    }

    operator fun times(their: Color): Color {
        return Color(r * their.r, g * their.g, b * their.b)
    }

    fun toDoubleArray(): DoubleArray {
        return doubleArrayOf(r, g, b)
    }

    fun toSRgbDoubles(): DoubleArray {
        fun convert(v: Double): Double {
            return if (v <= 0.0031308) {
                12.92 * v
            } else {
                1.055 * v.pow(1 / 2.4) - 0.055
            }
        }
        return doubleArrayOf(convert(r), convert(g), convert(b))
    }

    fun toSRgbInts(): IntArray {
        fun convert(v: Double): Int {
            return (v * 255).roundToInt().coerceIn(0, 255)
        }
        val (r, g, b) = toSRgbDoubles()
        return intArrayOf(convert(r), convert(g), convert(b))
    }

    fun isCloseTo(their: Color, delta: Double) : Boolean {
        return abs(r - their.r) <= delta
                && abs(g - their.g) <= delta
                && abs(b - their.b) <= delta
    }
}