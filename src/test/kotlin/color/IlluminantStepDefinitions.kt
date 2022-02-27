package garden.ephemeral.rocket.color

import assertk.assertThat
import assertk.assertions.isCloseTo
import garden.ephemeral.rocket.Constants.Companion.epsilon
import io.cucumber.java8.En

class IlluminantStepDefinitions : En {
    lateinit var illuminant: Illuminant

    init {
        Given("Standard Illuminant A") { illuminant = Illuminant.A }
        Given("Standard Illuminant D50") { illuminant = Illuminant.D50 }
        Given("Standard Illuminant D65") { illuminant = Illuminant.D65 }
        Given("Standard Illuminant F2") { illuminant = Illuminant.F2 }

        Then("illuminant.spectrum[{real}] = {real}") { wavelength: Double, expected: Double ->
            assertThat(illuminant.spectrum[wavelength]).isCloseTo(expected, epsilon)
        }
    }
}