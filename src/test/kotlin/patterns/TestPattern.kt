package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.linearRgb

class TestPattern: Pattern() {
    override fun patternAt(patternPoint: Tuple): Color {
        return linearRgb(patternPoint.x, patternPoint.y, patternPoint.z)
    }
}