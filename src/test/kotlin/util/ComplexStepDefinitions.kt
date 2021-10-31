package garden.ephemeral.rocket.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En

class ComplexStepDefinitions: En {
    lateinit var z: Complex
    lateinit var z1: Complex
    lateinit var z2: Complex

    init {
        ParameterType("complex", """(?x)
            | complex\( \s*
            |   (?:
            |     ($realRegex)
            |     (?:
            |       \s* ([+-]) \s* ((?:$realRegex \s*)? i)
            |     )?
            |   |
            |     (-?(?:$realRegex \s*)? i)
            |   )
            | \s* \)
            |""".trimMargin()) {
                realPartString: String?, joiner: String?,
                imaginaryPartString1: String?, imaginaryPartString2: String? ->

            fun imaginaryPartFromString(string: String): Double {
                if (!string.endsWith("i")) {
                    throw IllegalArgumentException("String should have ended with i");
                }
                return when (val front = string.substring(0, string.length - 1).trim()) {
                    "" -> 1.0
                    "-" -> -1.0
                    else -> realFromString(front)
                }
            }

            if (realPartString != null) {
                if (imaginaryPartString1 != null) {
                    val imaginarySign = if (joiner == "-") -1.0 else 1.0
                    Complex(realFromString(realPartString),
                        imaginarySign * imaginaryPartFromString(imaginaryPartString1))
                } else {
                    Complex(realFromString(realPartString), 0.0)
                }
            } else if (imaginaryPartString2 != null) {
                Complex(0.0, imaginaryPartFromString(imaginaryPartString2))
            } else {
                throw AssertionError("It shouldn't have matched the regex")
            }
        }

        When("z is {complex}") { complex: Complex ->
            z = complex
        }
        When("z1 is {complex}") { complex: Complex ->
            z1 = complex
        }
        When("z2 is {complex}") { complex: Complex ->
            z2 = complex
        }

        Then("z.to_string = {string}") { string: String ->
            assertThat(z.toString()).isEqualTo(string)
        }

        Then("z1 + z2 = {complex}") { complex: Complex ->
            assertThat(z1 + z2).isEqualTo(complex)
        }
        Then("z1 - z2 = {complex}") { complex: Complex ->
            assertThat(z1 - z2).isEqualTo(complex)
        }
        Then("z1 * z2 = {complex}") { complex: Complex ->
            assertThat(z1 * z2).isEqualTo(complex)
        }
        Then("z1 \\/ z2 = {complex}") { complex: Complex ->
            assertThat(z1 / z2).isEqualTo(complex)
        }

        Then("z.conjugate = {complex}") { complex: Complex ->
            assertThat(z.conjugate).isEqualTo(complex)
        }
    }
}