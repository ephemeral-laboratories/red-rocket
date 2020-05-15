package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import kotlin.math.floor

class CheckersPattern(val a: Tuple, val b: Tuple) : Pattern() {
    override fun patternAt(patternPoint: Tuple): Tuple {
        return if ((floor(patternPoint.x).toInt() +
                    floor(patternPoint.y).toInt() +
                    floor(patternPoint.z).toInt()) % 2 == 0) {
            a
        } else {
            b
        }
    }
}