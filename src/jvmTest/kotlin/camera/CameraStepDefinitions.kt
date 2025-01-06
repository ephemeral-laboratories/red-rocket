package garden.ephemeral.rocket.camera

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.isCloseTo
import garden.ephemeral.rocket.util.Angle
import garden.ephemeral.rocket.util.rad
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CameraStepDefinitions(space: Space) : En {
    private var hSize: Int = 0
    private var vSize: Int = 0
    private var fieldOfView: Angle = 0.0.rad
    private lateinit var camera: Camera
    private lateinit var image: Canvas

    init {
        Given("hsize ← {int}") { v: Int -> hSize = v }
        Given("vsize ← {int}") { v: Int -> vSize = v }
        Given("field_of_view ← {real}") { v: Double -> fieldOfView = v.rad }

        Given("camera ← camera\\({int}, {int}, {real})") { h: Int, v: Int, fov: Double ->
            camera = Camera(h, v, fov.rad)
        }
        Given("camera ← camera\\(hsize, vsize, field_of_view)") {
            camera = Camera(hSize, vSize, fieldOfView)
        }
        Given("camera ← camera\\({int}, {int}, {real}, multi\\({int}))") { h: Int, v: Int, fov: Double, sampleCount: Int ->
            camera = Camera(
                h,
                v,
                fov.rad,
                when (sampleCount) {
                    2 -> SamplingStrategy.multi2
                    4 -> SamplingStrategy.multi4
                    8 -> SamplingStrategy.multi8
                    16 -> SamplingStrategy.multi16
                    else -> throw IllegalArgumentException("Unsupported sample count: $sampleCount")
                }
            )
        }

        When("{ray_var} ← ray_for_pixel_offset\\(camera, {real}, {real})") { rv: String, x: Double, y: Double ->
            space.rays[rv] = camera.rayForPixelOffset(x, y)
        }

        When("camera.transform ← {transform} * {transform}") { m1: Matrix, m2: Matrix ->
            camera.transform = m1 * m2
        }
        When("camera.transform ← view_transform\\({tuple_var}, {tuple_var}, {tuple_var})") { tv1: String, tv2: String, tv3: String ->
            camera.transform = viewTransform(space.tuples[tv1]!!, space.tuples[tv2]!!, space.tuples[tv3]!!)
        }

        When("image ← render\\(camera, world)") {
            image = camera.render(space.world)
        }

        Then("camera.hsize = {int}") { e: Int -> assertThat(camera.hSize).isEqualTo(e) }
        Then("camera.vsize = {int}") { e: Int -> assertThat(camera.vSize).isEqualTo(e) }
        Then("camera.field_of_view = {real}") { e: Double -> assertThat(camera.fieldOfView).isEqualTo(e.rad) }

        Then("camera.transform = {matrix_var}") { mv: String ->
            assertThat(camera.transform).isEqualTo(space.matrices[mv]!!)
        }
        Then("camera.pixel_size = {real}") { e: Double ->
            assertThat(camera.pixelSize).isCloseTo(e, epsilon)
        }

        Then("color_at_pixel\\(camera, world, {int}, {int}) = {color}") { x: Int, y: Int, e: Color ->
            assertThat(camera.colorAtPixel(space.world, x, y)).isCloseTo(e, epsilon)
        }

        Then("pixel_at\\(image, {int}, {int}) = {color}") { x: Int, y: Int, e: Color ->
            assertThat(image.getPixel(x, y)).isCloseTo(e, epsilon)
        }
    }
}
