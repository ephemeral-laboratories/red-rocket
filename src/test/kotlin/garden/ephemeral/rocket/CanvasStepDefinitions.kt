package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.endsWith
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Tuple.Companion.color
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import io.cucumber.java8.En

class CanvasStepDefinitions: En {
    lateinit var canvas : Canvas
    lateinit var ppm : String

    init {
        Given("canvas ← canvas\\({int}, {int})") { width: Int, height: Int ->
            canvas = Canvas(width, height)
        }

        When("ppm ← canvas_to_ppm\\(canvas)") {
            ppm = canvas.toPPM()
        }

        When("write_pixel\\(canvas, {int}, {int}, {tuple_var})") { x: Int, y: Int, v: String ->
            canvas.setPixel(x, y, tuples[v]!!)
        }

        When("every pixel of canvas is set to color\\({real}, {real}, {real})") { r: Double, g: Double, b: Double ->
            canvas.fill(color(r, g, b))
        }

        Then("canvas.width = {int}") { e: Int -> assertThat(canvas.width).isEqualTo(e) }
        Then("canvas.height = {int}") { e: Int -> assertThat(canvas.height).isEqualTo(e) }

        Then("every pixel of canvas is {color}") { e: Tuple ->
            canvas.pixels.forEach { pixel: Tuple -> assertThat(pixel).isEqualTo(e) }
        }

        Then("pixel_at\\(canvas, {int}, {int}) = {tuple_var}") { x: Int, y: Int, v: String ->
            assertThat(canvas.getPixel(x, y)).isEqualTo(tuples[v]!!)
        }

        Then("lines {int}-{int} of ppm are") { start: Int, end: Int, expected: String ->
            val lines = ppm.lines().slice(IntRange(start - 1, end - 1))
                .joinToString("\n")
            assertThat(lines).isEqualTo(expected)
        }

        Then("ppm ends with a newline character") {
            assertThat(ppm).endsWith("\n")
        }
    }
}
