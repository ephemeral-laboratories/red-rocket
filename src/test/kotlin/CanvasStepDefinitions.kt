package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.endsWith
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.isCloseTo
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CanvasStepDefinitions(space: Space) : En {
    private lateinit var ppm: String

    init {
        Given("canvas ← canvas\\({int}, {int})") { width: Int, height: Int ->
            space.canvas = Canvas(width, height)
        }

        When("ppm ← canvas_to_ppm\\(canvas)") {
            ppm = space.canvas.toPPM()
        }
        When("canvas ← canvas_from_ppm\\({file_var})") { fv: String ->
            space.canvas = Canvas.fromPPM(space.files[fv]!!)
        }

        When("write_pixel\\(canvas, {int}, {int}, {color_var})") { x: Int, y: Int, v: String ->
            space.canvas.setPixel(x, y, space.colors[v]!!)
        }

        When("every pixel of canvas is set to {color}") { c: Color ->
            space.canvas.fill(c)
        }

        Then("canvas.width = {int}") { e: Int -> assertThat(space.canvas.width).isEqualTo(e) }
        Then("canvas.height = {int}") { e: Int -> assertThat(space.canvas.height).isEqualTo(e) }

        Then("every pixel of canvas is {color}") { e: Color ->
            space.canvas.pixels.forEach { pixel: Color -> assertThat(pixel).isCloseTo(e, epsilon) }
        }

        Then("pixel_at\\(canvas, {int}, {int}) = {color_var}") { x: Int, y: Int, v: String ->
            assertThat(space.canvas.getPixel(x, y)).isCloseTo(space.colors[v]!!, epsilon)
        }

        Then("lines {int}-{int} of ppm are") { start: Int, end: Int, expected: String ->
            val lines = ppm.lines().slice(IntRange(start - 1, end - 1))
                .joinToString("\n")
            assertThat(lines).isEqualTo(expected)
        }

        Then("ppm ends with a newline character") {
            assertThat(ppm).endsWith("\n")
        }

        Then("canvas_from_ppm\\({file_var}) should fail") { fv: String ->
            assertThat { Canvas.fromPPM(space.files[fv]!!) }.isFailure()
        }

        Then("pixel_at\\(canvas, {int}, {int}) = {color}") { x: Int, y: Int, e: Color ->
            assertThat(space.canvas.getPixel(x, y)).isCloseTo(e, epsilon)
        }
    }
}
