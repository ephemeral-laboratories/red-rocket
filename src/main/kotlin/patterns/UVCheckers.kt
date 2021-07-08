package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Color
import kotlin.math.floor

class UVCheckers(val width: Int, val height: Int, val a: Color, val b: Color): UVPattern() {
    override fun uvPatternAt(u: Double, v: Double): Color {
        return if ((floor(u * width).toInt() +
                    floor(v * height).toInt()) % 2 == 0) {
            a
        } else {
            b
        }
    }
}
