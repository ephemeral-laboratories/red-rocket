package garden.ephemeral.rocket.camera

import garden.ephemeral.rocket.color.Color

class Canvas(val width: Int, val height: Int) {
    private val data = DoubleArray(width * height * 3)

    private fun getOffset(x: Int, y: Int): Int = (y * width + x) * 3

    fun getPixel(x: Int, y: Int): Color {
        val offset = getOffset(x, y)
        return Color.linearRgb(data[offset], data[offset + 1], data[offset + 2])
    }

    fun setPixel(x: Int, y: Int, color: Color) {
        val offset = getOffset(x, y)
        color.toLinearRgbDoubles().copyInto(data, offset, 0, 3)
    }

    fun fill(color: Color) {
        val doubles = color.toLinearRgbDoubles()
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                val offset = getOffset(x, y)
                doubles.copyInto(data, offset, 0, 3)
            }
        }
    }

    val pixels: Sequence<Color>
        get() = IntRange(0, width - 1).asSequence()
            .zip(IntRange(0, height - 1).asSequence())
            .map { (x: Int, y: Int) -> getPixel(x, y) }

    companion object
}
