package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.asInputStream
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine
import kotlinx.io.readString
import kotlinx.io.writeString
import org.jetbrains.kotlinx.multik.ndarray.data.MemoryViewDoubleArray
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

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

    fun toPNG(sink: Sink) {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                val (r, g, b) = getPixel(x, y).toSRgbInts()
                image.setRGB(x, y, 0xff000000.toInt() + r.shl(16) + g.shl(8) + b)
            }
        }

        if (!ImageIO.write(image, "PNG", sink.asOutputStream())) {
            throw IllegalStateException("Couldn't find a suitable writer")
        }
    }

    fun toPNG(filePath: Path) {
        SystemFileSystem.sink(filePath).use { rawSink ->
            val sink = rawSink.buffered()
            toPNG(sink)
            sink.flush()
        }
    }

    private fun toPPM(sink: Sink) {
        val maximumLineLength = 70
        val lineBuffer = StringBuilder(80)

        fun Sink.writeLine(line: CharSequence) = writeString("$line\n")

        sink.writeLine("P3")
        sink.writeLine("$width $height")
        sink.writeLine("255")
        (0 until height).forEach { y: Int ->
            (0 until width).forEach { x: Int ->
                getPixel(x, y).toSRgbInts().forEach { i: Int ->
                    val nextValue = i.toString()
                    if (lineBuffer.length + 1 + nextValue.length >= maximumLineLength) {
                        sink.writeLine(lineBuffer)
                        lineBuffer.clear()
                    }
                    if (lineBuffer.isNotEmpty()) {
                        lineBuffer.append(' ')
                    }
                    lineBuffer.append(nextValue)
                }
            }
            if (lineBuffer.isNotEmpty()) {
                sink.writeLine(lineBuffer)
                lineBuffer.clear()
            }
        }
    }

    fun toPPM(filePath: Path) {
        SystemFileSystem.sink(filePath).use { rawSink ->
            val sink = rawSink.buffered()
            toPPM(sink)
            sink.flush()
        }
    }

    fun toPPM(): String {
        val buffer = Buffer()
        toPPM(buffer)
        return buffer.readString()
    }

    val pixels: Sequence<Color>
        get() = IntRange(0, width - 1).asSequence()
            .zip(IntRange(0, height - 1).asSequence())
            .map { (x: Int, y: Int) -> getPixel(x, y) }

    companion object {
        fun fromPNG(source: Source): Canvas {
            val image = source.asInputStream().use(ImageIO::read)
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

        fun fromPNG(filePath: Path) = SystemFileSystem.source(filePath).use { source ->
            fromPNG(source.buffered())
        }

        fun fromPPM(source: Source): Canvas {
            val whitespaceRegex = Regex("""\s""")
            val commentRegex = Regex("""#.*$""")
            val leftoverTokens = mutableListOf<String>()

            fun nextToken(): String {
                while (true) {
                    if (leftoverTokens.isNotEmpty()) {
                        return leftoverTokens.removeFirst()
                    }

                    val line = (source.readLine() ?: throw IllegalArgumentException("Expected more data"))
                        .replace(commentRegex, "")
                        .trim()
                    if (line.isNotEmpty()) {
                        leftoverTokens.addAll(line.split(whitespaceRegex).filter { it.isNotEmpty() })
                    }
                }
            }

            fun nextInt() = nextToken().toInt()

            val header = nextToken()
            require(header == "P3") { "Unexpected file header: $header" }

            val width = nextInt()
            val height = nextInt()
            val scale = nextInt()

            return Canvas(width, height).apply {
                (0 until height).forEach { y ->
                    (0 until width).forEach { x ->
                        val r = nextInt()
                        val g = nextInt()
                        val b = nextInt()
                        setPixel(x, y, Color.fromSRgbInts(r, g, b, scale))
                    }
                }
            }
        }

        fun fromPPM(filePath: Path) = SystemFileSystem.source(filePath).use { source ->
            fromPPM(source.buffered())
        }
    }
}
