package garden.ephemeral.rocket.color

import assertk.assertThat
import assertk.assertions.isCloseTo
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.spectra.atWavelength
import io.cucumber.java8.En

class IlluminantStepDefinitions : En {
    lateinit var illuminant: Illuminant

    init {
        Given("^(CIE Standard Illuminant [A-Z]+\\d*)$") { humanName: String ->
            illuminant = Illuminant.forHumanName(humanName)
        }

        Then("illuminant.spectrum[{real}] = {real}") { wavelength: Double, expected: Double ->
            assertThat(illuminant.spectrum().atWavelength(wavelength)).isCloseTo(expected, epsilon)
        }
    }
}
