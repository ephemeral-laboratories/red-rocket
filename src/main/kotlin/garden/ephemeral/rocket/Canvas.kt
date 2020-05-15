package garden.ephemeral.rocket

import garden.ephemeral.rocket.Tuple.Companion.color
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.*
import java.awt.image.DataBuffer


class Canvas(val width: Int, val height: Int) {
    private val raster: WritableRaster

    init {
        val bands = 4
        val bandOffsets = intArrayOf(0, 1, 2, 3) // length == bands, 0 == R, 1 == G, 2 == B and 3 == A
        val sampleModel: SampleModel = PixelInterleavedSampleModel(DataBuffer.TYPE_DOUBLE, width, height, bands, width * bands, bandOffsets)
        val buffer: DataBuffer = DataBufferDouble(width * height * bands)
        raster = Raster.createWritableRaster(sampleModel, buffer, null)
    }

    fun getPixel(x: Int, y: Int): Tuple {
        return color(raster.getPixel(x, y, null as DoubleArray?))
    }

    fun setPixel(x: Int, y: Int, color: Tuple) {
        raster.setPixel(x, y, color.cells)
    }

    fun fill(color: Tuple) {
        val doubles = color.cells
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                raster.setPixel(x, y, doubles)
            }
        }
    }

    fun toBufferedImage(): BufferedImage {
        val colorSpace = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB)
        val colorModel = ComponentColorModel(colorSpace, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_DOUBLE)
        return BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied, null)
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
                getPixel(x, y).toIntArray().sliceArray(IntRange(0, 2)).forEach { i: Int ->
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
