package org.trypticon.rocket.patterns

import org.trypticon.rocket.Tuple
import org.trypticon.rocket.Tuple.Companion.color

class TestPattern: Pattern() {
    override fun patternAt(patternPoint: Tuple): Tuple {
        return color(patternPoint.x, patternPoint.y, patternPoint.z)
    }
}