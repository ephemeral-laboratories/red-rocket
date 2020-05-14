package org.trypticon.rocket.shapes

import assertk.assertThat
import io.cucumber.java8.En
import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples
import org.trypticon.rocket.isCloseTo
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class TriangleStepDefinitions: En {
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