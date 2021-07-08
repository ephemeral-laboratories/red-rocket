package garden.ephemeral.rocket

import kotlin.math.abs

data class Color(val r: Double, val g: Double, val b: Double) {
    val isBlack:     Boolean get() = r == 0.0 && g == 0.0 && b == 0.0
    val isNonBlack:  Boolean get() = !isBlack

    companion object {
        val black = Color(0.0, 0.0, 0.0)
        val white = Color(1.0, 1.0, 1.0)

        fun grey(v: Double): Color { return Color(v, v, v) }
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

    fun toIntArray(): IntArray {
        return intArrayOf(floatToClampedInt(r), floatToClampedInt(g), floatToClampedInt(b))
    }

    private fun floatToClampedInt(f: Double): Int {
        return (f * 256).toInt().coerceIn(0, 255)
    }

    fun isCloseTo(their: Color, delta: Double) : Boolean {
        return abs(r - their.r) <= delta
                && abs(g - their.g) <= delta
                && abs(b - their.b) <= delta
    }
}