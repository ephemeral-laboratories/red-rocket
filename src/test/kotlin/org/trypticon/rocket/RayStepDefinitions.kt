package org.trypticon.rocket

import assertk.assertThat
import io.cucumber.java8.En
import org.trypticon.rocket.CommonParameterTypes.Companion.epsilon
import org.trypticon.rocket.MatrixStepDefinitions.Companion.matrices
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples

class RayStepDefinitions: En {
    companion object {
        val rays : MutableMap<String, Ray> = mutableMapOf()
    }

    init {
        ParameterType("ray_var", "r\\d*") { string -> string }

        When("{ray_var} ← ray\\({tuple_var}, {tuple_var})") { rv: String, tv1: String, tv2: String ->
            rays[rv] = Ray(tuples[tv1]!!, tuples[tv2]!!)
        }
        Given("{ray_var} ← ray\\({point}, {vector})") { rv: String, p: Tuple, v: Tuple ->
            rays[rv] = Ray(p, v)
        }

        When("{ray_var} ← transform\\({ray_var}, {matrix_var})") { rv1: String, rv2: String, mv: String ->
            rays[rv1] = rays[rv2]!!.transform(matrices[mv]!!)
        }

        Then("{ray_var}.origin = {tuple_var}") { rv: String, tv: String ->
            assertThat(rays[rv]!!.origin).isCloseTo(tuples[tv]!!, epsilon)
        }
        Then("{ray_var}.origin = {point}") { rv: String, p: Tuple ->
            assertThat(rays[rv]!!.origin).isCloseTo(p, epsilon)
        }

        Then("{ray_var}.direction = {tuple_var}") { rv: String, tv: String ->
            assertThat(rays[rv]!!.direction).isCloseTo(tuples[tv]!!, epsilon)
        }
        Then("{ray_var}.direction = {vector}") { rv: String, v: Tuple ->
            assertThat(rays[rv]!!.direction).isCloseTo(v, epsilon)
        }

        Then("position\\({ray_var}, {real}) = {point}") { rv: String, t: Double, e: Tuple ->
            assertThat(rays[rv]!!.position(t)).isCloseTo(e, epsilon)
        }
    }
}