package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.isCloseTo
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.java8.En

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