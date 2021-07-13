package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.Tuple
import kotlin.math.floor
import kotlin.math.sqrt

class RingPattern(val a: Color, val b: Color) : Pattern() {
    override fun patternAt(patternPoint: Tuple): Color {
        return if (floor(sqrt(patternPoint.x * patternPoint.x + patternPoint.z * patternPoint.z)).toInt() % 2 == 0) {
            a
        } else {
            b
        }
    }
}