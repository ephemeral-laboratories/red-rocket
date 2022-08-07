package garden.ephemeral.rocket.shapes

import assertk.assertThat
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.isCloseTo
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.java8.En

class TriangleStepDefinitions : En {
    init {
        Given("{shape_var} ← triangle\\({point}, {point}, {point})") {
                sv: String, p1: Tuple, p2: Tuple, p3: Tuple ->
            shapes[sv] = Triangle(p1, p2, p3)
        }
        Given("{shape_var} ← triangle\\({tuple_var}, {tuple_var}, {tuple_var})") {
                sv: String, tv1: String, tv2: String, tv3: String ->
            shapes[sv] = Triangle(tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!)
        }

        Then("{shape_var}.normal = {vector}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as Triangle).normal).isCloseTo(e, epsilon)
        }
        Then("{tuple_var} = {shape_var}.normal") { tv: String, sv: String ->
            assertThat(tuples[tv]!!).isCloseTo((shapes[sv] as Triangle).normal, epsilon)
        }
    }
}
