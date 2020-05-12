package org.trypticon.rocket

import org.trypticon.rocket.Tuple.Companion.color
import java.nio.DoubleBuffer

class Canvas(val width: Int, val height: Int) {
    val bpp : Int = 4
    val stride : Int get() = width * bpp
    var buffer: DoubleBuffer = DoubleBuffer.allocate(height * stride)

    fun pixelPosition(x: Int, y: Int) {
        buffer.position(x * bpp + y * stride)
    }

    fun getPixel(x: Int, y: Int): Tuple {
        pixelPosition(x, y)
        val rgb = DoubleArray(4)
        buffer.get(rgb)
        return color(rgb)
    }

    fun setPixel(x: Int, y: Int, color: Tuple) {
        pixelPosition(x, y)
        buffer.put(color.cells)
    }

    fun fill(color: Tuple) {
        buffer.clear()
        buffer.position(0)
        while (buffer.remaining() > 0) {
            buffer.put(color.cells)
        }
    }

    fun toPPM(): String {
        val maximumLineLength: Int = 70
        val builder = StringBuilder()
        val lineBuffer = StringBuilder(80)
        builder.append("P3\n")
        builder.append(width).append(' ').append(height).append('\n')
        builder.append("255\n")
        (0 until height).forEach { y: Int ->
            lineBuffer.clear()
            (0 until width).forEach { x: Int ->
                getPixel(x, y).toInts().sliceArray(IntRange(0, 2)).forEach { i: Int ->
                    val nextValue = i.toString()
                    if (lineBuffer.length + 1 + nextValue.length >= maximumLineLength) {
                        builder.append(lineBuffer).append('\n')
                        lineBuffer.clear()
                    }
                    if (lineBuffer.isNotEmpty()) {
                        lineBuffer.append(' ')
                    }
                    lineBuffer.append(nextValue)
                }
            }
            if (lineBuffer.isNotEmpty()) {
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
