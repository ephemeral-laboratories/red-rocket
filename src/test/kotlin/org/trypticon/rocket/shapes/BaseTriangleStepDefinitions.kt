package org.trypticon.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples
import org.trypticon.rocket.isCloseTo
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class BaseTriangleStepDefinitions: En {
    init {
        Then("{shape_var}.p1 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as BaseTriangle).p1).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.p2 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as BaseTriangle).p2).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.p3 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as BaseTriangle).p3).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.e1 = {vector}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as BaseTriangle).e1).isCloseTo(e, epsilon)
        }
        Then("{shape_var}.e2 = {vector}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as BaseTriangle).e2).isCloseTo(e, epsilon)
        }
    }
}