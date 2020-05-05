
import Tuple.Companion.color
import TupleStepDefinitions.Companion.tuples
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En

class CanvasStepDefinitions: En {
    lateinit var c : Canvas
    lateinit var ppm : String

    init {
        Given("c ← canvas\\({int}, {int})") { width: Int, height: Int -> c = Canvas(width, height) }

        When("ppm ← canvas_to_ppm\\(c)") {
            ppm = c.toPPM()
        }

        When("write_pixel\\(c, {int}, {int}, {var})") { x: Int, y: Int, v: String -> c.setPixel(x, y, tuples[v]!!) }

        Then("c.width = {int}") { e: Int -> assertThat(c.width).isEqualTo(e) }
        Then("c.height = {int}") { e: Int -> assertThat(c.height).isEqualTo(e) }

        Then("every pixel of c is color\\({real}, {real}, {real})") { r: Double, g: Double, b: Double ->
            val e = color(r, g, b)
            c.pixels.forEach { pixel: Tuple -> assertThat(pixel).isEqualTo(e) }
        }

        Then("pixel_at\\(c, {int}, {int}) = {var}") { x: Int, y: Int, v: String -> assertThat(c.getPixel(x, y)).isEqualTo(tuples[v]!!)}

        Then("lines {int}-{int} of ppm are") { start: Int, end: Int, expected: String ->
            val lines = ppm.lines().slice(IntRange(start - 1, end - 1))
                .joinToString("\n")
            assertThat(lines).isEqualTo(expected)
        }
    }
}
