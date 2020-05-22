package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import kotlin.math.floor

class UVCheckers(val width: Int, val height: Int, val a:Tuple, val b: Tuple): UVPattern() {
    override fun uvPatternAt(u: Double, v: Double): Tuple {
        return if ((floor(u * width).toInt() +
                    floor(v * height).toInt()) % 2 == 0) {
            a
        } else {
            b
        }
    }
}
