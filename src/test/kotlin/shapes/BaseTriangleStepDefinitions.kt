package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Universe
import garden.ephemeral.rocket.isCloseTo
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class BaseTriangleStepDefinitions(universe: Universe) : En {
    init {
        Then("{shape_var}.point1 = {tuple_var}") { sv: String, tv: String ->
            assertThat((universe.shapes[sv] as BaseTriangle).point1).isEqualTo(universe.tuples[tv]!!)
        }
        Then("{shape_var}.point2 = {tuple_var}") { sv: String, tv: String ->
            assertThat((universe.shapes[sv] as BaseTriangle).point2).isEqualTo(universe.tuples[tv]!!)
        }
        Then("{shape_var}.point3 = {tuple_var}") { sv: String, tv: String ->
            assertThat((universe.shapes[sv] as BaseTriangle).point3).isEqualTo(universe.tuples[tv]!!)
        }
        Then("{shape_var}.edge1 = {vector}") { sv: String, e: Tuple ->
            assertThat((universe.shapes[sv] as BaseTriangle).edge1).isCloseTo(e, epsilon)
        }
        Then("{shape_var}.edge2 = {vector}") { sv: String, e: Tuple ->
            assertThat((universe.shapes[sv] as BaseTriangle).edge2).isCloseTo(e, epsilon)
        }
    }
}
