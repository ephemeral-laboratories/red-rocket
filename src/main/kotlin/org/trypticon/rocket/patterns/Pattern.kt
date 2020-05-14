package org.trypticon.rocket.patterns

import org.trypticon.rocket.Matrix
import org.trypticon.rocket.Matrix.Companion.identity4x4
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.shapes.Shape

abstract class Pattern {
    var transform: Matrix = identity4x4

    abstract fun patternAt(patternPoint: Tuple): Tuple

    fun patternAtShape(shape: Shape, worldPoint: Tuple): Tuple {
        val objectPoint = shape.worldToObject(worldPoint)
        val patternPoint = transform.inverse * objectPoint
        return patternAt(patternPoint)
    }
}