package garden.ephemeral.rocket.util

import assertk.assertThat
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.isCloseTo
import io.cucumber.java8.En

class StokesStepDefinitions: En {
    lateinit var stokes: Tuple
    init {
        When("S is a Stokes vector for unpolarized light") {
            stokes = Stokes.Unpolarized
        }
        When("S is a Stokes vector for linearly polarized \\(horizontal) light") {
            stokes = Stokes.LinearlyPolarizedHorizontal
        }
        When("S is a Stokes vector for linearly polarized \\(vertical) light") {
            stokes = Stokes.LinearlyPolarizedVertical
        }
        When("S is a Stokes vector for linearly polarized \\(+45°) light") {
            stokes = Stokes.LinearlyPolarizedPlus45
        }
        When("S is a Stokes vector for linearly polarized \\(-45°) light") {
            stokes = Stokes.LinearlyPolarizedMinus45
        }
        When("S is a Stokes vector for circularly polarized \\(right hand) light") {
            stokes = Stokes.CircularlyPolarizedRight
        }
        When("S is a Stokes vector for circularly polarized \\(left hand) light") {
            stokes = Stokes.CircularlyPolarizedLeft
        }

        Then("S = \\({real}, {real}, {real}, {real})") { i: Double, q: Double, u: Double, v: Double ->
            assertThat(stokes).isCloseTo(Tuple(i, q, u, v), epsilon)
        }
    }
}