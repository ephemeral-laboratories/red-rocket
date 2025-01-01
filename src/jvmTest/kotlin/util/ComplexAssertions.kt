package garden.ephemeral.rocket.util

import assertk.Assert
import assertk.assertAll
import assertk.assertions.isCloseTo
import assertk.assertions.prop

fun Assert<Complex>.isCloseTo(expected: Complex, delta: Double) {
    assertAll {
        prop(Complex::real).isCloseTo(expected.real, delta)
        prop(Complex::imaginary).isCloseTo(expected.imaginary, delta)
    }
}
