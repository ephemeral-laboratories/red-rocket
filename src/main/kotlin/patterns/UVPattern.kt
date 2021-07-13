package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.color.Color

abstract class UVPattern {
    abstract fun uvPatternAt(u: Double, v: Double): Color
}