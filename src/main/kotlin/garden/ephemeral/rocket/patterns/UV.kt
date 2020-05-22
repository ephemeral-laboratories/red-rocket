package garden.ephemeral.rocket.patterns

class UV(val u: Double, val v: Double) {
    operator fun component1() = u
    operator fun component2() = v
}