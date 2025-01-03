package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Constants.epsilon
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

infix fun Angle.shouldBeCloseTo(expected: Angle) {
    radians shouldBe (expected.radians plusOrMinus epsilon)
}
