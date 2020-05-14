package org.trypticon.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class SmoothTriangleStepDefinitions: En {
    init {
        Given("{shape_var} ← smooth_triangle\\({point}, {point}, {point}, {vector}, {vector}, {vector})") {
                sv: String, p1: Tuple, p2: Tuple, p3: Tuple, n1: Tuple, n2: Tuple, n3: Tuple ->
            shapes[sv] = SmoothTriangle(p1, p2, p3, n1, n2, n3)
        }
        Given("{shape_var} ← smooth_triangle\\({tuple_var}, {tuple_var}, {tuple_var}, " +
                "{tuple_var}, {tuple_var}, {tuple_var})") {
                sv: String, tv1: String, tv2: String, tv3: String, tv4: String, tv5: String, tv6: String ->
            shapes[sv] = SmoothTriangle(
                tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!,
                tuples[tv4]!!, tuples[tv5]!!, tuples[tv6]!!)
        }

        Then("{shape_var}.n1 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as SmoothTriangle).n1).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.n2 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as SmoothTriangle).n2).isEqualTo(tuples[tv]!!)
        }
        Then("{shape_var}.n3 = {tuple_var}") { sv: String, tv: String ->
            assertThat((shapes[sv] as SmoothTriangle).n3).isEqualTo(tuples[tv]!!)
        }
    }
}