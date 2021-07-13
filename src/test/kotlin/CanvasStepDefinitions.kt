package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.endsWith
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.FileStepDefinitions.Companion.files
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.ColorStepDefinitions.Companion.colors
import garden.ephemeral.rocket.color.isCloseTo
import io.cucumber.java8.En

class CanvasStepDefinitions: En {
    companion object {
        lateinit var canvas : Canvas
        lateinit var ppm : String
    }

    init {
        Given("canvas ← canvas\\({int}, {int})") { width: Int, height: Int ->
            canvas = Canvas(width, height)
        }

        When("ppm ← canvas_to_ppm\\(canvas)") {
            ppm = canvas.toPPM()
        }
        When("canvas ← canvas_from_ppm\\({file_var})") { fv: String ->
            canvas = Canvas.fromPPM(files[fv]!!)
        }

        When("write_pixel\\(canvas, {int}, {int}, {color_var})") { x: Int, y: Int, v: String ->
            canvas.setPixel(x, y, colors[v]!!)
        }

        When("every pixel of canvas is set to {color}") { c: Color ->
            canvas.fill(c)
        }

        Then("canvas.width = {int}") { e: Int -> assertThat(canvas.width).isEqualTo(e) }
        Then("canvas.height = {int}") { e: Int -> assertThat(canvas.height).isEqualTo(e) }

        Then("every pixel of canvas is {color}") { e: Color ->
            canvas.pixels.forEach { pixel: Color -> assertThat(pixel).isCloseTo(e, epsilon) }
        }

        Then("pixel_at\\(canvas, {int}, {int}) = {color_var}") { x: Int, y: Int, v: String ->
            assertThat(canvas.getPixel(x, y)).isCloseTo(colors[v]!!, epsilon)
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
            assertThat { Canvas.fromPPM(files[fv]!!) }.isFailure()
        }

        Then("pixel_at\\(canvas, {int}, {int}) = {color}") { x: Int, y: Int, e: Color ->
            assertThat(canvas.getPixel(x, y)).isCloseTo(e, epsilon)
        }
    }
}
