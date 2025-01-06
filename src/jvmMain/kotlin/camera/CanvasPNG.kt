package garden.ephemeral.rocket.camera

import garden.ephemeral.rocket.color.Color
import kotlinx.io.asInputStream
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

fun Canvas.toPNG(file: Path) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    (0 until height).forEach { y ->
        (0 until width).forEach { x ->
            val (r, g, b) = getPixel(x, y).toSRgbInts()
            image.setRGB(x, y, 0xff000000.toInt() + r.shl(16) + g.shl(8) + b)
        }
    }
    SystemFileSystem.sink(file).use { rawSink ->
        rawSink.buffered().use { sink ->
            sink.asOutputStream().use { stream ->
                if (!ImageIO.write(image, "PNG", stream)) {
                    throw IllegalStateException("Couldn't find a suitable writer")
                }
            }
        }
    }
}

fun Canvas.Companion.fromPNG(file: Path): Canvas {
    val image = SystemFileSystem.source(file).use { rawSource ->
        rawSource.buffered().use { source ->
            source.asInputStream().use(ImageIO::read)
        }
    }
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
