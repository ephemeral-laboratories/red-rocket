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

    fun setPixel(x: Int, y: Int, color: Tuple) {
        pixelPosition(x, y)
        buffer.put(color.r).put(color.g).put(color.b).put(color.a)
    }

    fun fill(color: Tuple) {
        buffer.clear()
        buffer.position(0)
        while (buffer.remaining() > 0) {
            buffer.put(color.r).put(color.g).put(color.b).put(color.a)
        }
    }

    fun toPPM(): String {
        val maximumLineLength: Int = 70
        val builder = StringBuilder()
        val lineBuffer = StringBuilder(80)
        builder.append("P3\n")
        builder.append(width).append(' ').append(height).append('\n')
        builder.append("255\n")
        IntRange(0, height - 1).forEach { y: Int ->
            lineBuffer.clear()
            IntRange(0, width - 1).forEach { x: Int ->
                getPixel(x, y).toInts().sliceArray(IntRange(0, 2)).forEach { i: Int ->
                    val nextValue = i.toString()
                    if (lineBuffer.length + 1 + nextValue.length >= maximumLineLength) {
                        builder.append(lineBuffer).append('\n')
                        lineBuffer.clear()
                    }
                    if (lineBuffer.length > 0) {
                        lineBuffer.append(' ')
                    }
                    lineBuffer.append(nextValue)
                }
            }
            if (lineBuffer.length > 0) {
                builder.append(lineBuffer).append('\n')
            }
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
