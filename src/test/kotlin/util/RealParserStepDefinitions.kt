package garden.ephemeral.rocket.util

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Constants.Companion.epsilon
import io.cucumber.java8.En
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.POSITIVE_INFINITY

class RealParserStepDefinitions : En {
    lateinit var string: String

    init {
        Given("a string to parse {string}") { string: String -> this.string = string }

        Then("parsing the string should produce {double}") { expected: Double ->
            assertThat(RealParser.realFromString(string)).isCloseTo(expected, epsilon)
        }
        Then("parsing the string should produce NaN") {
            assertThat(RealParser.realFromString(string)).isNaN()
        }
        Then("parsing the string should produce Infinity") {
            assertThat(RealParser.realFromString(string)).isEqualTo(POSITIVE_INFINITY)
        }
        Then("parsing the string should produce -Infinity") {
            assertThat(RealParser.realFromString(string)).isEqualTo(NEGATIVE_INFINITY)
        }
    }
}
