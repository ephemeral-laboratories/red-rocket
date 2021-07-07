package garden.ephemeral.rocket

import kotlin.math.abs

data class Color(val cells: DoubleArray) {
    val r: Double  get() = cells[0]
    val g: Double  get() = cells[1]
    val b: Double  get() = cells[2]
    val isBlack:     Boolean get() = r == 0.0 && g == 0.0 && b == 0.0
    val isNonBlack:  Boolean get() = !isBlack

    constructor(r: Double, g: Double, b: Double) : this(doubleArrayOf(r, g, b))

    companion object {
        val black = Color(0.0, 0.0, 0.0)
        val white = Color(1.0, 1.0, 1.0)

        fun grey(v: Double): Color { return Color(v, v, v) }
    }

    operator fun plus(their: Color): Color {
        return binaryOp(their) { a, b -> a + b }
    }

    operator fun minus(their: Color): Color {
        return binaryOp(their) { a, b -> a - b }
    }

    operator fun times(scalar: Double): Color {
        return unaryOp { a -> a * scalar }
    }

    operator fun times(their: Color): Color {
        return binaryOp(their) { a, b -> a * b }
    }

    private fun unaryOp(op: (Double) -> Double): Color {
        val result = DoubleArray(3) { index -> op(cells[index]) }
        return Color(result)
    }

    private fun binaryOp(their: Color, op: (Double, Double) -> Double): Color {
        val result = DoubleArray(3) { index -> op(cells[index], their.cells[index]) }
        return Color(result)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color

        if (!cells.contentEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.contentHashCode()
    }
}