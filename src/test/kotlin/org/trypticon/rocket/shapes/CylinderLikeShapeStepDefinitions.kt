package org.trypticon.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class CylinderLikeShapeStepDefinitions: En {
    init {
        Given("{shape_var}.minimum ← {real}") { sv: String, v: Double ->
            (shapes[sv]!! as CylinderLikeShape).minimum = v
        }
        Given("{shape_var}.maximum ← {real}") { sv: String, v: Double ->
            (shapes[sv]!! as CylinderLikeShape).maximum = v
        }
        Given("{shape_var}.closed ← {boolean}") { sv: String, v: Boolean ->
            (shapes[sv]!! as CylinderLikeShape).closed = v
        }

        Then("{shape_var}.minimum = {real}") { sv: String, e: Double ->
            assertThat((shapes[sv]!! as CylinderLikeShape).minimum).isEqualTo(e)
        }
        Then("{shape_var}.maximum = {real}") { sv: String, e: Double ->
            assertThat((shapes[sv]!! as CylinderLikeShape).maximum).isEqualTo(e)
        }
        Then("{shape_var}.closed = {boolean}") { sv: String, e: Boolean ->
            assertThat((shapes[sv]!! as CylinderLikeShape).closed).isEqualTo(e)
        }
    }
}