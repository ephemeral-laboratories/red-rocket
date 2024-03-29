package garden.ephemeral.rocket.color

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.support.expected
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En
import kotlin.math.abs

// Constructed reflectively
@Suppress("unused")
class ColorStepDefinitions(space: Space) : En {
    init {
        ParameterType(
            "color_var",
            "(?:red|blue|green|cyan|magenta|yellow|brown|purple|black|white|color|" +
                "intensity|result)\\d?"
        ) { string ->
            string
        }

        ParameterType("color", "(linear_rgb_color|srgb_color|cie_xyz_color)\\(($realRegex),\\s*($realRegex),\\s*($realRegex)\\)") {
                type, s1: String, s2: String, s3: String ->
            when (type) {
                "linear_rgb_color" -> linearRgb(realFromString(s1), realFromString(s2), realFromString(s3))
                "srgb_color" -> Color.srgb(realFromString(s1), realFromString(s2), realFromString(s3))
                "cie_xyz_color" -> Color.cieXyz(realFromString(s1), realFromString(s2), realFromString(s3))
                else -> throw UnsupportedOperationException("Missing case")
            }
        }

        ParameterType("array3", "\\(($realRegex),\\s*($realRegex),\\s*($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            doubleArrayOf(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        Given("{color_var} ← {color}") { cv: String, v: Color -> space.colors[cv] = v }

        When("{color_var} ← color_to_cie_xyz\\({color_var})") { cv1: String, cv2: String ->
            space.colors[cv1] = space.colors[cv2]!!.toCieXyz()
        }

        Then("{color_var} = {color}") { cv: String, e: Color ->
            assertThat(space.colors[cv]!!).isCloseTo(e, epsilon)
        }

        Then("{color_var} = {color_var}") { cv1: String, cv2: String ->
            assertThat(space.colors[cv1]!!).isCloseTo(space.colors[cv2]!!, epsilon)
        }

        Then("{color_var} + {color_var} = {color}") { cv1: String, cv2: String, e: Color ->
            assertThat(space.colors[cv1]!! + space.colors[cv2]!!).isCloseTo(e, epsilon)
        }

        Then("{color_var} - {color_var} = {color}") { cv1: String, cv2: String, e: Color ->
            assertThat(space.colors[cv1]!! - space.colors[cv2]!!).isCloseTo(e, epsilon)
        }

        Then("{color_var} * {real} = {color}") { cv: String, s: Double, t: Color ->
            assertThat(space.colors[cv]!! * s).isCloseTo(t, epsilon)
        }

        Then("{color_var} * {color_var} = {color}") { cv1: String, cv2: String, t: Color ->
            assertThat(space.colors[cv1]!! * space.colors[cv2]!!).isCloseTo(t, epsilon)
        }

        Then("{color_var}.red = {real}") { cv: String, e: Double ->
            val c = space.colors[cv]!!
            assertThat(c).isInstanceOf(RgbColor::class)
            assertThat((c as RgbColor).r).isCloseTo(e, epsilon)
        }
        Then("{color_var}.green = {real}") { cv: String, e: Double ->
            val c = space.colors[cv]!!
            assertThat(c).isInstanceOf(RgbColor::class)
            assertThat((c as RgbColor).g).isCloseTo(e, epsilon)
        }
        Then("{color_var}.blue = {real}") { cv: String, e: Double ->
            val c = space.colors[cv]!!
            assertThat(c).isInstanceOf(RgbColor::class)
            assertThat((c as RgbColor).b).isCloseTo(e, epsilon)
        }

        Then("{color_var}.x = {real}") { cv: String, e: Double ->
            val c = space.colors[cv]!!
            assertThat(c).isInstanceOf(CieXyzColor::class)
            assertThat((c as CieXyzColor).x).isCloseTo(e, epsilon)
        }
        Then("{color_var}.y = {real}") { cv: String, e: Double ->
            val c = space.colors[cv]!!
            assertThat(c).isInstanceOf(CieXyzColor::class)
            assertThat((c as CieXyzColor).y).isCloseTo(e, epsilon)
        }
        Then("{color_var}.z = {real}") { cv: String, e: Double ->
            val c = space.colors[cv]!!
            assertThat(c).isInstanceOf(CieXyzColor::class)
            assertThat((c as CieXyzColor).z).isCloseTo(e, epsilon)
        }

        Then("color_to_srgb_doubles\\({color_var}) = {array3}") { cv: String, array: DoubleArray ->
            val srgb = space.colors[cv]!!.toSRgbDoubles()
            assertThat(srgb.size).isEqualTo(3)
            assertThat(srgb[0]).isCloseTo(array[0], epsilon)
            assertThat(srgb[1]).isCloseTo(array[1], epsilon)
            assertThat(srgb[2]).isCloseTo(array[2], epsilon)
        }
    }
}

fun Assert<Color>.isCloseTo(expected: Color, delta: Double) = given { actual ->
    if (actual.isCloseTo(expected, delta)) return
    expected("close to: $expected but was: $actual")
}

fun Color.isCloseTo(their: Color, delta: Double): Boolean {
    if (javaClass != their.javaClass) {
        return false
    }

    val rgb = toLinearRgbDoubles()
    val theirRgb = their.toLinearRgbDoubles()
    if (rgb.size != theirRgb.size) {
        return false
    }

    rgb.indices.forEach { i ->
        if (abs(rgb[i] - theirRgb[i]) > delta) {
            return false
        }
    }

    return true
}
