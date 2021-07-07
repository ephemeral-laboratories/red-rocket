package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Color
import garden.ephemeral.rocket.Tuple

class TestPattern: Pattern() {
    override fun patternAt(patternPoint: Tuple): Color {
        return Color(patternPoint.x, patternPoint.y, patternPoint.z)
    }
}