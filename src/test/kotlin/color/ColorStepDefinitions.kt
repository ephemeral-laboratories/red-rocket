package garden.ephemeral.rocket.color

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.matchesPredicate
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En

class ColorStepDefinitions: En {
    companion object {
        val colors: MutableMap<String, Color> = mutableMapOf()
    }

    init {
        ParameterType(
            "color_var",
            "(?:red|blue|green|cyan|magenta|yellow|brown|purple|black|white|color|" +
                    "intensity|result)\\d?"
        ) { string ->
            string
        }

        ParameterType("color", "color\\(($realRegex),\\s*($realRegex),\\s*($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            Color(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        Given("{color_var} ← {color}")  { cv: String, v: Color -> colors[cv] = v }

        Then("{color_var} = {color}")  { cv: String, e: Color -> assertThat(colors[cv]!!).isCloseTo(e, epsilon) }

        Then("{color_var} = {color_var}")  { cv1: String, cv2: String ->
            assertThat(colors[cv1]!!).isCloseTo(colors[cv2]!!, epsilon)
        }

        Then("{color_var} + {color_var} = {color}") { cv1: String, cv2: String, e: Color ->
            assertThat(colors[cv1]!! + colors[cv2]!!).isCloseTo(e, epsilon)
        }

        Then("{color_var} - {color_var} = {color}") { cv1: String, cv2: String, e: Color ->
            assertThat(colors[cv1]!! - colors[cv2]!!).isCloseTo(e, epsilon)
        }

        Then("{color_var} * {real} = {color}") { cv: String, s: Double, t: Color ->
            assertThat(colors[cv]!! * s).isCloseTo(t, epsilon)
        }

        Then("{color_var} * {color_var} = {color}") { cv1: String, cv2: String, t: Color ->
            assertThat(colors[cv1]!! * colors[cv2]!!).isCloseTo(t, epsilon)
        }

        Then("{color_var}.red = {real}") { cv: String, e: Double -> assertThat(colors[cv]!!.r).isCloseTo(e, epsilon) }
        Then("{color_var}.green = {real}") { cv: String, e: Double -> assertThat(colors[cv]!!.g).isCloseTo(e, epsilon) }
        Then("{color_var}.blue = {real}") { cv: String, e: Double -> assertThat(colors[cv]!!.b).isCloseTo(e, epsilon) }
    }
}

fun Assert<Color>.isCloseTo(expected: Color, delta: Double) {
    this.matchesPredicate { c: Color -> c.isCloseTo(expected, delta) }
}
