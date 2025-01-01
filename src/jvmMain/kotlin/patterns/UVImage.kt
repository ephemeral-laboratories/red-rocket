package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Canvas
import garden.ephemeral.rocket.color.Color
import kotlin.math.round

class UVImage(val canvas: Canvas) : UVPattern() {
    override fun uvPatternAt(u: Double, v: Double): Color {
        val x = u * (canvas.width - 1)
        // V is bottom to top, image data is top to bottom
        val y = (1 - v) * (canvas.height - 1)
        return canvas.getPixel(round(x).toInt(), round(y).toInt())
    }
}
