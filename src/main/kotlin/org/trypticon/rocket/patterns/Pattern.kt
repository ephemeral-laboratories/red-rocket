package org.trypticon.rocket.patterns

import org.trypticon.rocket.Matrix
import org.trypticon.rocket.Shape
import org.trypticon.rocket.Tuple

abstract class Pattern {
    var transform: Matrix =
        Matrix.identity4x4

    abstract fun patternAt(patternPoint: Tuple): Tuple

    fun patternAtShape(shape: Shape, worldPoint: Tuple): Tuple {
        val objectPoint = shape.transform.inverse() * worldPoint
        val patternPoint = transform.inverse() * objectPoint
        return patternAt(patternPoint)
    }
}