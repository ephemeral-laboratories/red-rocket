package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.Tuple
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class SmoothTriangleStepDefinitions(space: Space) : En {
    init {
        Given("{shape_var} ← smooth_triangle\\({point}, {point}, {point}, {vector}, {vector}, {vector})") {
                sv: String, p1: Tuple, p2: Tuple, p3: Tuple, n1: Tuple, n2: Tuple, n3: Tuple ->
            space.shapes[sv] = SmoothTriangle(p1, p2, p3, null, null, null, n1, n2, n3)
        }
        Given(
            "{shape_var} ← smooth_triangle\\(" +
                "{tuple_var}, {tuple_var}, {tuple_var}, " +
                "{tuple_var}, {tuple_var}, {tuple_var})"
        ) { sv: String, tv1: String, tv2: String, tv3: String, tv4: String, tv5: String, tv6: String ->
            space.shapes[sv] = SmoothTriangle(
                space.tuples[tv1]!!, space.tuples[tv2]!!, space.tuples[tv3]!!,
                null, null, null,
                space.tuples[tv4]!!, space.tuples[tv5]!!, space.tuples[tv6]!!
            )
        }

        Then("{shape_var}.normal1 = {tuple_var}") { sv: String, tv: String ->
            assertThat((space.shapes[sv] as SmoothTriangle).normal1).isEqualTo(space.tuples[tv]!!)
        }
        Then("{shape_var}.normal2 = {tuple_var}") { sv: String, tv: String ->
            assertThat((space.shapes[sv] as SmoothTriangle).normal2).isEqualTo(space.tuples[tv]!!)
        }
        Then("{shape_var}.normal3 = {tuple_var}") { sv: String, tv: String ->
            assertThat((space.shapes[sv] as SmoothTriangle).normal3).isEqualTo(space.tuples[tv]!!)
        }
    }
}
