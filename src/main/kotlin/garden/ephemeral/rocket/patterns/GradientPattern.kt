package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import kotlin.math.floor

class GradientPattern(val a: Tuple, val b: Tuple) : Pattern() {
    override fun patternAt(patternPoint: Tuple): Tuple {
        val distance = b - a
        val fraction = patternPoint.x - floor(patternPoint.x)
        return a + distance * fraction
    }
}