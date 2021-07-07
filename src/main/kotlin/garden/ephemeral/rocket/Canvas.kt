package garden.ephemeral.rocket

import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.regex.Pattern
import javax.imageio.ImageIO


class Canvas(val width: Int, val height: Int) {
    private val raster: WritableRaster

    init {
        val bands = 3
        val bandOffsets = intArrayOf(0, 1, 2) // length == bands, 0 == R, 1 == G and 2 == B
        val sampleModel: SampleModel = PixelInterleavedSampleModel(DataBuffer.TYPE_DOUBLE, width, height, bands, width * bands, bandOffsets)
        val buffer: DataBuffer = DataBufferDouble(width * height * bands)
        raster = Raster.createWritableRaster(sampleModel, buffer, null)
    }

    fun getPixel(x: Int, y: Int): Color {
        return Color(raster.getPixel(x, y, null as DoubleArray?))
    }

    fun setPixel(x: Int, y: Int, color: Color) {
        raster.setPixel(x, y, color.cells)
    }

    fun fill(color: Color) {
        val doubles = color.cells
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                raster.setPixel(x, y, doubles)
            }
        }
    }

    fun toBufferedImage(): BufferedImage {
        val colorSpace = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB)
        val colorModel = ComponentColorModel(colorSpace, true, false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE)
        return BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied, null)
    }

    fun toPNG(file: File) {
        ImageIO.write(toBufferedImage(), "PNG", file)
    }

    fun toPPM(file: File) {
        file.printWriter().use(this@Canvas::toPPM)
    }

    fun toPPM(): String {
        val stringWriter = StringWriter()
        PrintWriter(stringWriter).use(this@Canvas::toPPM)
        return stringWriter.toString()
    }

    private fun toPPM(writer: PrintWriter) {
        val maximumLineLength: Int = 70
        val lineBuffer = StringBuilder(80)
        writer.println("P3")
        writer.println("$width $height")
        writer.println("255")
        (0 until height).forEach { y: Int ->
            lineBuffer.clear()
            (0 until width).forEach { x: Int ->
                getPixel(x, y).toIntArray().forEach { i: Int ->
                    val nextValue = i.toString()
                    if (lineBuffer.length + 1 + nextValue.length >= maximumLineLength) {
                        writer.println(lineBuffer)
                        lineBuffer.clear()
                    }
                    if (lineBuffer.isNotEmpty()) {
                        lineBuffer.append(' ')
                    }
                    lineBuffer.append(nextValue)
                }
            }
            if (lineBuffer.isNotEmpty()) {
                writer.println(lineBuffer)
            }
        }
    }

    val pixels: Iterable<Color>
        get() {
            return IntRange(0, width - 1)
                .zip(IntRange(0, height - 1))
                .map {(x: Int, y: Int) -> getPixel(x, y)}
        }

    companion object {
        fun fromPNG(file: File): Canvas {
            val image = ImageIO.read(file)
            return Canvas(image.width, image.height).apply {
                raster.setRect(image.raster)
            }
        }

        fun fromPPM(file: File): Canvas {
            return Scanner(file.bufferedReader()).use { scanner ->
                val comments = Pattern.compile("\\s*(#.*\n)*")

                scanner.skip(comments)
                val header = scanner.nextLine()
                if (header != "P3") {
                    throw IllegalArgumentException("Unexpected file header: $header")
                }

                scanner.skip(comments)
                val width = scanner.nextInt()
                scanner.skip(comments)
                val height = scanner.nextInt()
                scanner.skip(comments)
                val scale = scanner.nextInt().toDouble()

                Canvas(width, height).apply {
                    (0 until height).forEach { y ->
                        (0 until width).forEach { x ->
                            scanner.skip(comments)
                            val r = scanner.nextInt() / scale
                            scanner.skip(comments)
                            val g = scanner.nextInt() / scale
                            scanner.skip(comments)
                            val b = scanner.nextInt() / scale
                            setPixel(x, y, Color(r, g, b))
                        }
                    }
                }
            }
        }
    }
}
