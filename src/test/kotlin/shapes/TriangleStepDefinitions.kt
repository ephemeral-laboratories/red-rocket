package garden.ephemeral.rocket.shapes

import assertk.assertThat
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.isCloseTo
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class TriangleStepDefinitions(space: Space) : En {
    init {
        Given("{shape_var} ← triangle\\({point}, {point}, {point})") {
                sv: String, p1: Tuple, p2: Tuple, p3: Tuple ->
            space.shapes[sv] = Triangle(p1, p2, p3)
        }
        Given("{shape_var} ← triangle\\({tuple_var}, {tuple_var}, {tuple_var})") {
                sv: String, tv1: String, tv2: String, tv3: String ->
            space.shapes[sv] = Triangle(space.tuples[tv1]!!, space.tuples[tv2]!!, space.tuples[tv3]!!)
        }

        Then("{shape_var}.normal = {vector}") { sv: String, e: Tuple ->
            assertThat((space.shapes[sv] as Triangle).normal).isCloseTo(e, epsilon)
        }
        Then("{tuple_var} = {shape_var}.normal") { tv: String, sv: String ->
            assertThat(space.tuples[tv]!!).isCloseTo((space.shapes[sv] as Triangle).normal, epsilon)
        }
    }
}
