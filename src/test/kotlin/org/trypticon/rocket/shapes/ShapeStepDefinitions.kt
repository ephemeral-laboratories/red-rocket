package org.trypticon.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.IntersectionStepDefinitions.Companion.xs
import org.trypticon.rocket.MaterialStepDefinitions.Companion.m
import org.trypticon.rocket.Matrix
import org.trypticon.rocket.MatrixStepDefinitions.Companion.matrices
import org.trypticon.rocket.RayStepDefinitions.Companion.rays
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples

class ShapeStepDefinitions: En {
    companion object {
        val shapes: MutableMap<String, Shape> = mutableMapOf()
        const val shapeVarRegex = "[ps]\\d*|shape|outer|inner|object"
    }

    init {
        ParameterType("shape_var", shapeVarRegex) { string -> string }

        Given("^($shapeVarRegex) ← test_shape\\(\\)") { sv: String -> shapes[sv] = TestShape() }

        Given("{shape_var}.material.ambient ← {real}") { sv: String, v: Double ->
            shapes[sv]!!.material.ambient = v
        }

        When("set_transform\\({shape_var}, {matrix_var})") { sv: String, mv: String ->
            shapes[sv]!!.transform = matrices[mv]!!
        }
        When("set_transform\\({shape_var}, {translation})") { sv: String, m: Matrix ->
            shapes[sv]!!.transform = m
        }
        When("set_transform\\({shape_var}, {scaling})") { sv: String, m: Matrix ->
            shapes[sv]!!.transform = m
        }

        When("{tuple_var} ← normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = shapes[sv]!!.worldNormalAt(p)
        }

        When("m ← {shape_var}.material") { sv: String ->
            m = shapes[sv]!!.material
        }
        When("{shape_var}.material ← m") { sv: String ->
            shapes[sv]!!.material = m
        }

        When("xs ← local_intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            xs = shapes[sv]!!.localIntersect(rays[rv]!!)
        }
        When("{tuple_var} ← local_normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = shapes[sv]!!.localNormalAt(p)
        }

        Then("{shape_var}.transform = {matrix_var}") { sv: String, mv: String ->
            assertThat(shapes[sv]!!.transform).isEqualTo(matrices[mv])
        }
        Then("{shape_var}.transform = {translation}") { sv: String, e: Matrix ->
            assertThat(shapes[sv]!!.transform).isEqualTo(e)
        }

        Then("{shape_var}.material = m") { sv: String ->
            assertThat(shapes[sv]!!.material).isEqualTo(m)
        }
        Then("{tuple_var} = {shape_var}.material.color") { tv: String, sv: String ->
            assertThat(tuples[tv]!!).isEqualTo(shapes[sv]!!.material.color)
        }

        Then("{shape_var}.saved_ray.origin = {point}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as TestShape).savedRay!!.origin).isEqualTo(e)
        }
        Then("{shape_var}.saved_ray.direction = {vector}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as TestShape).savedRay!!.direction).isEqualTo(e)
        }
    }
}