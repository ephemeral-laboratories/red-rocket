package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.MatrixStepDefinitions.Companion.matrices
import garden.ephemeral.rocket.RayStepDefinitions.Companion.rays
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.WorldStepDefinitions.Companion.world
import io.cucumber.java8.En

class CameraStepDefinitions: En {
    var hSize: Int = 0
    var vSize: Int = 0
    var fieldOfView: Double = 0.0
    lateinit var camera: Camera
    lateinit var image: Canvas

    init {
        Given("hsize ← {int}") { v: Int -> hSize = v }
        Given("vsize ← {int}") { v: Int -> vSize = v }
        Given("field_of_view ← {real}") { v: Double -> fieldOfView = v }

        Given("camera ← camera\\({int}, {int}, {real})") { h: Int, v: Int, fov: Double ->
            camera = Camera(h, v, fov)
        }
        Given("camera ← camera\\(hsize, vsize, field_of_view)") {
            camera = Camera(hSize, vSize, fieldOfView)
        }

        When("{ray_var} ← ray_for_pixel\\(camera, {int}, {int})") { rv: String, x: Int, y: Int ->
            rays[rv] = camera.rayForPixel(x, y)
        }

        When("camera.transform ← {transform} * {transform}") { m1: Matrix, m2: Matrix ->
            camera.transform = m1 * m2
        }
        When("camera.transform ← view_transform\\({tuple_var}, {tuple_var}, {tuple_var})") {
                tv1: String, tv2: String, tv3: String ->
            camera.transform = viewTransform(tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!)
        }

        When("image ← render\\(camera, world)") {
            image = camera.render(world)
        }

        Then("camera.hsize = {int}") { e: Int -> assertThat(camera.hSize).isEqualTo(e) }
        Then("camera.vsize = {int}") { e: Int -> assertThat(camera.vSize).isEqualTo(e) }
        Then("camera.field_of_view = {real}") { e: Double -> assertThat(camera.fieldOfView).isEqualTo(e) }

        Then("camera.transform = {matrix_var}") { mv: String ->
            assertThat(camera.transform).isEqualTo(matrices[mv]!!)
        }
        Then("camera.pixel_size = {real}") { e: Double ->
            assertThat(camera.pixelSize).isCloseTo(e, epsilon)
        }

        Then("pixel_at\\(image, {int}, {int}) = {color}") { x: Int, y: Int, e: Color ->
            assertThat(image.getPixel(x, y)).isCloseTo(e, epsilon)
        }
    }
}