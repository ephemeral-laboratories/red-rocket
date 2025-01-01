package garden.ephemeral.rocket.util

import assertk.Assert
import assertk.assertions.isCloseTo
import assertk.assertions.prop

fun Assert<Angle>.isCloseTo(expected: Angle, delta: Double) {
    prop(Angle::radians).isCloseTo(expected.radians, delta)
}
