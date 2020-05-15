package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.color

class TestPattern: Pattern() {
    override fun patternAt(patternPoint: Tuple): Tuple {
        return color(patternPoint.x, patternPoint.y, patternPoint.z)
    }
}