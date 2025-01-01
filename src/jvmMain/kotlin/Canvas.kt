package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import org.jetbrains.kotlinx.multik.ndarray.data.MemoryViewDoubleArray
import java.awt.image.BufferedImage
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Path
import java.util.Scanner
import java.util.regex.Pattern
import java.util.stream.Stream
import javax.imageio.ImageIO
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

class Canvas(val width: Int, val height: Int) {
    private val data = DoubleArray(width * height * 3)
    private val dataView = MemoryViewDoubleArray(data)

    private fun getOffset(x: Int, y: Int): Int = (y * width + x) * 3

    fun getPixel(x: Int, y: Int): Color {
        val offset = getOffset(x, y)
        return linearRgb(data[offset], data[offset + 1], data[offset + 2])
    }

    fun setPixel(x: Int, y: Int, color: Color) {
        val destinationOffset = getOffset(x, y)
        val source = color.toLinearRgbDoubles().data
        source.copyInto(dataView, destinationOffset)
    }

    fun fill(color: Color) {
        val source = color.toLinearRgbDoubles().data
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                val destinationOffset = getOffset(x, y)
                source.copyInto(dataView, destinationOffset)
            }
        }
    }

    fun toPNG(file: Path) {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                val (r, g, b) = getPixel(x, y).toSRgbInts()
                image.setRGB(x, y, 0xff000000.toInt() + r.shl(16) + g.shl(8) + b)
            }
        }
        file.outputStream().use { stream ->
            if (!ImageIO.write(image, "PNG", stream)) {
                throw IllegalStateException("Couldn't find a suitable writer")
            }
        }
    }

    fun toPPM(file: Path) {
        PrintWriter(file.bufferedWriter()).use(this@Canvas::toPPM)
    }

    fun toPPM(): String {
        val stringWriter = StringWriter()
        PrintWriter(stringWriter).use(this@Canvas::toPPM)
        return stringWriter.toString()
    }

    private fun toPPM(writer: PrintWriter) {
        val maximumLineLength = 70
        val lineBuffer = StringBuilder(80)
        writer.println("P3")
        writer.println("$width $height")
        writer.println("255")
        (0 until height).forEach { y: Int ->
            lineBuffer.clear()
            (0 until width).forEach { x: Int ->
                getPixel(x, y).toSRgbInts().forEach { i: Int ->
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

    val pixels: Stream<Color>
        get() {
            return IntRange(0, width - 1)
                .zip(IntRange(0, height - 1))
                .stream()
                .map { (x: Int, y: Int) -> getPixel(x, y) }
        }

    companion object {
        fun fromPNG(file: Path): Canvas {
            val image = file.inputStream().use(ImageIO::read)
            return Canvas(image.width, image.height).apply {
                (0 until height).forEach { y: Int ->
                    (0 until width).forEach { x: Int ->
                        val rgb = image.getRGB(x, y)
                        setPixel(
                            x,
                            y,
                            Color.fromSRgbInts(
                                rgb.shr(16).and(0xff),
                                rgb.shr(8).and(0xff),
                                rgb.and(0xFF)
                            )
                        )
                    }
                }
            }
        }

        fun fromPPM(file: Path): Canvas {
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
                val scale = scanner.nextInt()

                Canvas(width, height).apply {
                    (0 until height).forEach { y ->
                        (0 until width).forEach { x ->
                            scanner.skip(comments)
                            val r = scanner.nextInt()
                            scanner.skip(comments)
                            val g = scanner.nextInt()
                            scanner.skip(comments)
                            val b = scanner.nextInt()
                            setPixel(x, y, Color.fromSRgbInts(r, g, b, scale))
                        }
                    }
                }
            }
        }
    }
}
