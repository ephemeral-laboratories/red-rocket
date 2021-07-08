package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Color
import garden.ephemeral.rocket.Tuple
import kotlin.math.floor

class GradientPattern(val a: Color, val b: Color) : Pattern() {
    override fun patternAt(patternPoint: Tuple): Color {
        val distance = b - a
        val fraction = patternPoint.x - floor(patternPoint.x)
        return a + distance * fraction
    }
}