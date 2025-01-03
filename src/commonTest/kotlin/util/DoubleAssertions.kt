package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Constants.epsilon
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

infix fun Double.shouldBeCloseTo(expected: Double) {
    this shouldBe (expected plusOrMinus epsilon)
}
