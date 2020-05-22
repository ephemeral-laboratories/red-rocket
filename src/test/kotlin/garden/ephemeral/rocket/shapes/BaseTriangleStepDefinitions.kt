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
        Then("{shape_var}.point1 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as BaseTriangle).point1).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.point2 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as BaseTriangle).point2).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.point3 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as BaseTriangle).point3).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.edge1 = {vector}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as BaseTriangle).edge1).isCloseTo(e, epsilon)
        }
        Then("{shape_var}.edge2 = {vector}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as BaseTriangle).edge2).isCloseTo(e, epsilon)
        }
    }
}