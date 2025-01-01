package garden.ephemeral.rocket

import assertk.assertThat
import garden.ephemeral.rocket.Constants.epsilon
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class RayStepDefinitions(space: Space) : En {
    init {
        ParameterType("ray_var", "ray\\d*") { string -> string }

        When("{ray_var} ← ray\\({tuple_var}, {tuple_var})") { rv: String, tv1: String, tv2: String ->
            space.rays[rv] = Ray(space.tuples[tv1]!!, space.tuples[tv2]!!)
        }
        Given("{ray_var} ← ray\\({point}, {vector})") { rv: String, p: Tuple, v: Tuple ->
            space.rays[rv] = Ray(p, v)
        }
        Given("{ray_var} ← ray\\({point}, {tuple_var})") { rv: String, p: Tuple, tv: String ->
            space.rays[rv] = Ray(p, space.tuples[tv]!!)
        }

        When("{ray_var} ← transform\\({ray_var}, {matrix_var})") { rv1: String, rv2: String, mv: String ->
            space.rays[rv1] = space.rays[rv2]!!.transform(space.matrices[mv]!!)
        }

        Then("{ray_var}.origin = {tuple_var}") { rv: String, tv: String ->
            assertThat(space.rays[rv]!!.origin).isCloseTo(space.tuples[tv]!!, epsilon)
        }
        Then("{ray_var}.origin = {point}") { rv: String, p: Tuple ->
            assertThat(space.rays[rv]!!.origin).isCloseTo(p, epsilon)
        }

        Then("{ray_var}.direction = {tuple_var}") { rv: String, tv: String ->
            assertThat(space.rays[rv]!!.direction).isCloseTo(space.tuples[tv]!!, epsilon)
        }
        Then("{ray_var}.direction = {vector}") { rv: String, v: Tuple ->
            assertThat(space.rays[rv]!!.direction).isCloseTo(v, epsilon)
        }

        Then("position\\({ray_var}, {real}) = {point}") { rv: String, t: Double, e: Tuple ->
            assertThat(space.rays[rv]!!.position(t)).isCloseTo(e, epsilon)
        }
    }
}
