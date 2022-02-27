package garden.ephemeral.rocket.color

import assertk.assertThat
import garden.ephemeral.rocket.Constants
import io.cucumber.java8.En

class ColorMatchingFunctionStepDefinitions : En {
    lateinit var cmf: ColorMatchingFunction

    init {
        Given("Color matching function {string}") { humanName: String ->
            cmf = ColorMatchingFunction.forHumanName(humanName)
        }

        Then("cmf.spectrum[{real}] = {color}") { wavelength: Double, expected: CieXyzColor ->
            assertThat(cmf.spectrum[wavelength]).isCloseTo(expected, Constants.epsilon)
        }
    }
}
