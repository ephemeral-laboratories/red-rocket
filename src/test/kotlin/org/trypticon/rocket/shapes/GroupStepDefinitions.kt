package org.trypticon.rocket.shapes

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class GroupStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← group\\(\\)") { sv: String ->
            shapes[sv] = Group()
        }

        When("add_child\\({shape_var}, {shape_var})") { sv1: String, sv2: String ->
            (shapes[sv1] as Group).addChild(shapes[sv2]!!)
        }

        Then("{shape_var} is empty") { sv: String ->
            assertThat((shapes[sv] as Group).children).isEmpty()
        }
        Then("{shape_var} is not empty") { sv: String ->
            assertThat((shapes[sv] as Group).children).isNotEmpty()
        }
        Then ("{shape_var} includes {shape_var}") { sv1: String, sv2: String ->
            assertThat((shapes[sv1] as Group).children).contains(shapes[sv2]!!)
        }
    }
}