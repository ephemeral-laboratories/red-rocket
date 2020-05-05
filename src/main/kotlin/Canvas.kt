@file:Suppress("EXPERIMENTAL_API_USAGE")

import java.nio.DoubleBuffer

class Canvas(val width: Int, val height: Int) {
    val bpp : Int = 4
    val stride : Int get() = width * 4
    var buffer: DoubleBuffer = DoubleBuffer.allocate(width * height * 4)

    fun pixelPosition(x: Int, y: Int) {
        buffer.position(x * bpp + y * stride)
    }

    fun getPixel(x: Int, y: Int): Tuple {
        pixelPosition(x, y)
        return Tuple(buffer.get(), buffer.get(), buffer.get(), buffer.get())
    }

    fun setPixel(x: Int, y: Int, v: Tuple) {
        pixelPosition(x, y)
        buffer.put(v.r).put(v.g).put(v.b).put(v.a)
    }

    fun toPPM(): String {
        val builder = StringBuilder()
        builder.append("P3\n")
        builder.append("${width} ${height}\n")
        builder.append("255\n")
        IntRange(0, height - 1).forEach { y: Int ->
            builder.append(IntRange(0, width - 1)
                .map { x: Int -> getPixel(x, y).toIntPPM() }
                .joinToString(separator = " ", postfix = "\n"))
        }
        return builder.toString()
    }

    val pixels: Iterable<Tuple>
        get() {
            return IntRange(0, width - 1)
                .zip(IntRange(0, height - 1))
                .map {(x: Int, y: Int) -> getPixel(x, y)}
        }
}
