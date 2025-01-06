package garden.ephemeral.rocket.camera

import garden.ephemeral.rocket.color.Color
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine
import kotlinx.io.readString
import kotlinx.io.writeString

private fun Sink.writeLine(line: CharSequence) {
    writeString(line)
    writeString("\n")
}

private fun Canvas.toPPM(sink: Sink) {
    val maximumLineLength = 70
    val lineBuffer = StringBuilder(80)
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

fun Canvas.toPPM(file: Path) {
    SystemFileSystem.sink(file).use { rawSink ->
        rawSink.buffered().use(::toPPM)
    }
}

fun Canvas.toPPM(): String {
    return Buffer().use { buffer ->
        toPPM(buffer)
        buffer.flush()
        buffer.readString()
    }
}

fun Canvas.Companion.fromPPM(source: Source): Canvas {
    val whitespaceRegex = Regex("""\s+""")
    val commentRegex = Regex("""#.*$""")
    val unusedTokens = mutableListOf<String>()

    fun nextToken(): String {
        while (true) {
            unusedTokens.removeFirstOrNull()?.let { return(it) }

            val line = (source.readLine() ?: throw AssertionError("No lines left"))
                .replace(commentRegex, "").trim()
            if (line.isNotEmpty()) {
                unusedTokens.addAll(line.split(whitespaceRegex))
            }
        }
    }

    fun nextInt() = nextToken().toInt()

    val header = nextToken()
    if (header != "P3") {
        throw IllegalArgumentException("Unexpected file header: $header")
    }

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

fun Canvas.Companion.fromPPM(file: Path): Canvas {
    SystemFileSystem.source(file).use { rawSource ->
        return fromPPM(rawSource.buffered())
    }
}
