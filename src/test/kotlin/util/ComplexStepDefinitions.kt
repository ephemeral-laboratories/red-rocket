package garden.ephemeral.rocket.util

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En

class ComplexStepDefinitions : En {
    lateinit var z: Complex
    lateinit var z1: Complex
    lateinit var z2: Complex

    init {
        ParameterType(
            "complex",
            """(?x)
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
            |
            """.trimMargin()
        ) {
                realPartString: String?, joiner: String?,
                imaginaryPartString1: String?, imaginaryPartString2: String? ->

            fun imaginaryPartFromString(string: String): Double {
                if (!string.endsWith("i")) {
                    throw IllegalArgumentException("String should have ended with i")
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
                    Complex(
                        realFromString(realPartString),
                        imaginarySign * imaginaryPartFromString(imaginaryPartString1)
                    )
                } else {
                    Complex(realFromString(realPartString), 0.0)
                }
            } else if (imaginaryPartString2 != null) {
                Complex(0.0, imaginaryPartFromString(imaginaryPartString2))
            } else {
                throw AssertionError("It shouldn't have matched the regex")
            }
        }

        When("z ← {complex}") { complex: Complex ->
            z = complex
        }
        When("z1 ← {complex}") { complex: Complex ->
            z1 = complex
        }
        When("z2 ← {complex}") { complex: Complex ->
            z2 = complex
        }

        When("z ← complex_from_polar\\({real}, {real})") { magnitude: Double, argument: Double ->
            z = Complex.fromPolar(magnitude, argument.rad)
        }

        Then("z = {complex}") { expected: Complex ->
            assertThat(z).isCloseTo(expected, epsilon)
        }
        Then("z.to_string = {string}") { string: String ->
            assertThat(z.toString()).isEqualTo(string)
        }

        Then("z1 + z2 = {complex}") { complex: Complex ->
            assertThat(z1 + z2).isCloseTo(complex, epsilon)
        }
        Then("z + {real} = {complex}") { real: Double, complex: Complex ->
            assertThat(z + real).isCloseTo(complex, epsilon)
        }
        Then("{real} + z = {complex}") { real: Double, complex: Complex ->
            assertThat(real + z).isCloseTo(complex, epsilon)
        }
        Then("z1 - z2 = {complex}") { complex: Complex ->
            assertThat(z1 - z2).isCloseTo(complex, epsilon)
        }
        Then("z - {real} = {complex}") { real: Double, complex: Complex ->
            assertThat(z - real).isCloseTo(complex, epsilon)
        }
        Then("{real} - z = {complex}") { real: Double, complex: Complex ->
            assertThat(real - z).isCloseTo(complex, epsilon)
        }
        Then("z1 * z2 = {complex}") { complex: Complex ->
            assertThat(z1 * z2).isCloseTo(complex, epsilon)
        }
        Then("z * {real} = {complex}") { real: Double, complex: Complex ->
            assertThat(z * real).isCloseTo(complex, epsilon)
        }
        Then("{real} * z = {complex}") { real: Double, complex: Complex ->
            assertThat(real * z).isCloseTo(complex, epsilon)
        }
        Then("z1 \\/ z2 = {complex}") { complex: Complex ->
            assertThat(z1 / z2).isCloseTo(complex, epsilon)
        }
        Then("z \\/ {real} = {complex}") { real: Double, complex: Complex ->
            assertThat(z / real).isCloseTo(complex, epsilon)
        }
        Then("{real} \\/ z = {complex}") { real: Double, complex: Complex ->
            assertThat(real / z).isCloseTo(complex, epsilon)
        }

        Then("z.conjugate = {complex}") { expected: Complex ->
            assertThat(z.conjugate).isCloseTo(expected, epsilon)
        }
        Then("z.magnitude = {real}") { expected: Double ->
            assertThat(z.magnitude).isCloseTo(expected, epsilon)
        }
        Then("z.argument = {real}") { expected: Double ->
            assertThat(z.argument).isCloseTo(expected.rad, epsilon)
        }
        Then("z.argument = NaN") {
            assertThat(z.argument.radians).isNaN()
        }

        Then("z^{real} = {complex}") { power: Double, complex: Complex ->
            assertThat(z.pow(power)).isCloseTo(complex, epsilon)
        }
        mapOf<String, (Complex) -> Complex>(
            "sqrt" to ::sqrt,
            "sin" to ::sin,
            "cos" to ::cos,
            "tan" to ::tan,
            "ln" to ::ln,
            "arcsin" to ::arcsin,
            "arccos" to ::arccos,
            "arctan" to ::arctan
        ).forEach { (fName, f) ->
            Then("$fName\\(z) = {complex}") { complex: Complex ->
                assertThat(f(z)).isCloseTo(complex, epsilon)
            }
        }
        Then("sqrt\\({real}) = z") { real: Double ->
            assertThat(complexSqrt(real)).isCloseTo(z, epsilon)
        }
    }
}
