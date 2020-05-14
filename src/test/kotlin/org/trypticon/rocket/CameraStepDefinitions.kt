package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.MatrixStepDefinitions.Companion.matrices
import org.trypticon.rocket.RayStepDefinitions.Companion.rays
import org.trypticon.rocket.Transforms.Companion.viewTransform
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples
import org.trypticon.rocket.WorldStepDefinitions.Companion.w

class CameraStepDefinitions: En {
    var hSize: Int = 0
    var vSize: Int = 0
    var fieldOfView: Double = 0.0
    lateinit var c: Camera
    lateinit var image: Canvas

    init {
        Given("hsize ← {int}") { v: Int -> hSize = v }
        Given("vsize ← {int}") { v: Int -> vSize = v }
        Given("field_of_view ← {real}") { v: Double -> fieldOfView = v }

        Given("c ← camera\\({int}, {int}, {real})") { h: Int, v: Int, fov: Double ->
            c = Camera(h, v, fov)
        }
        Given("c ← camera\\(hsize, vsize, field_of_view)") {
            c = Camera(hSize, vSize, fieldOfView)
        }

        When("{ray_var} ← ray_for_pixel\\(c, {int}, {int})") { rv: String, x: Int, y: Int ->
            rays[rv] = c.rayForPixel(x, y)
        }

        When("c.transform ← {transform} * {transform}") { m1: Matrix, m2: Matrix ->
            c.transform = m1 * m2
        }
        When("c.transform ← view_transform\\({tuple_var}, {tuple_var}, {tuple_var})") {
                tv1: String, tv2: String, tv3: String ->
            c.transform = viewTransform(tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!)
        }

        When("image ← render\\(c, w)") {
            image = c.render(w)
        }

        Then("c.hsize = {int}") { e: Int -> assertThat(c.hSize).isEqualTo(e) }
        Then("c.vsize = {int}") { e: Int -> assertThat(c.vSize).isEqualTo(e) }
        Then("c.field_of_view = {real}") { e: Double -> assertThat(c.fieldOfView).isEqualTo(e) }

        Then("c.transform = {matrix_var}") { mv: String ->
            assertThat(c.transform).isEqualTo(matrices[mv]!!)
        }
        Then("c.pixel_size = {real}") { e: Double ->
            assertThat(c.pixelSize).isCloseTo(e, epsilon)
        }

        Then("pixel_at\\(image, {int}, {int}) = {color}") { x: Int, y: Int, e: Tuple ->
            assertThat(image.getPixel(x, y)).isCloseTo(e, epsilon)
        }
    }
}