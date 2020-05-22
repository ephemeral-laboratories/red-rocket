package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple

abstract class UVPattern {
    abstract fun uvPatternAt(u: Double, v: Double): Tuple;
}