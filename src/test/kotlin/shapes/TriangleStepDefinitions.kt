package garden.ephemeral.rocket.shapes

import assertk.assertThat
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Universe
import garden.ephemeral.rocket.isCloseTo
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class TriangleStepDefinitions(universe: Universe) : En {
    init {
        Given("{shape_var} ← triangle\\({point}, {point}, {point})") {
                sv: String, p1: Tuple, p2: Tuple, p3: Tuple ->
            universe.shapes[sv] = Triangle(p1, p2, p3)
        }
        Given("{shape_var} ← triangle\\({tuple_var}, {tuple_var}, {tuple_var})") {
                sv: String, tv1: String, tv2: String, tv3: String ->
            universe.shapes[sv] = Triangle(universe.tuples[tv1]!!, universe.tuples[tv2]!!, universe.tuples[tv3]!!)
        }

        Then("{shape_var}.normal = {vector}") { sv: String, e: Tuple ->
            assertThat((universe.shapes[sv] as Triangle).normal).isCloseTo(e, epsilon)
        }
        Then("{tuple_var} = {shape_var}.normal") { tv: String, sv: String ->
            assertThat(universe.tuples[tv]!!).isCloseTo((universe.shapes[sv] as Triangle).normal, epsilon)
        }
    }
}
