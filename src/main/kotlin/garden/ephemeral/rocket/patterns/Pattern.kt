package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.Matrix.Companion.identity4x4
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.shapes.Shape

abstract class Pattern {
    var transform: Matrix = identity4x4

    abstract fun patternAt(patternPoint: Tuple): Tuple

    fun patternAtShape(shape: Shape, worldPoint: Tuple): Tuple {
        val objectPoint = shape.worldToObject(worldPoint)
        val patternPoint = transform.inverse * objectPoint
        return patternAt(patternPoint)
    }
}