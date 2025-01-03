package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Constants.epsilon
import io.kotest.assertions.all
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
suspend infix fun Complex.shouldBeCloseTo(expected: Complex) = all {
    real shouldBe (expected.real plusOrMinus epsilon)
    imaginary shouldBe (expected.imaginary plusOrMinus epsilon)
}
