package garden.ephemeral.rocket

import assertk.assertThat
import garden.ephemeral.rocket.Constants.epsilon
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class RayStepDefinitions(universe: Universe) : En {
    init {
        ParameterType("ray_var", "ray\\d*") { string -> string }

        When("{ray_var} ← ray\\({tuple_var}, {tuple_var})") { rv: String, tv1: String, tv2: String ->
            universe.rays[rv] = Ray(universe.tuples[tv1]!!, universe.tuples[tv2]!!)
        }
        Given("{ray_var} ← ray\\({point}, {vector})") { rv: String, p: Tuple, v: Tuple ->
            universe.rays[rv] = Ray(p, v)
        }
        Given("{ray_var} ← ray\\({point}, {tuple_var})") { rv: String, p: Tuple, tv: String ->
            universe.rays[rv] = Ray(p, universe.tuples[tv]!!)
        }

        When("{ray_var} ← transform\\({ray_var}, {matrix_var})") { rv1: String, rv2: String, mv: String ->
            universe.rays[rv1] = universe.rays[rv2]!!.transform(universe.matrices[mv]!!)
        }

        Then("{ray_var}.origin = {tuple_var}") { rv: String, tv: String ->
            assertThat(universe.rays[rv]!!.origin).isCloseTo(universe.tuples[tv]!!, epsilon)
        }
        Then("{ray_var}.origin = {point}") { rv: String, p: Tuple ->
            assertThat(universe.rays[rv]!!.origin).isCloseTo(p, epsilon)
        }

        Then("{ray_var}.direction = {tuple_var}") { rv: String, tv: String ->
            assertThat(universe.rays[rv]!!.direction).isCloseTo(universe.tuples[tv]!!, epsilon)
        }
        Then("{ray_var}.direction = {vector}") { rv: String, v: Tuple ->
            assertThat(universe.rays[rv]!!.direction).isCloseTo(v, epsilon)
        }

        Then("position\\({ray_var}, {real}) = {point}") { rv: String, t: Double, e: Tuple ->
            assertThat(universe.rays[rv]!!.position(t)).isCloseTo(e, epsilon)
        }
    }
}
