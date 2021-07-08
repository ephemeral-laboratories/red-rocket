package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Color
import garden.ephemeral.rocket.Tuple
import kotlin.math.floor

class CheckersPattern(val a: Color, val b: Color) : Pattern() {
    override fun patternAt(patternPoint: Tuple): Color {
        return if ((floor(patternPoint.x).toInt() +
                    floor(patternPoint.y).toInt() +
                    floor(patternPoint.z).toInt()) % 2 == 0) {
            a
        } else {
            b
        }
    }
}