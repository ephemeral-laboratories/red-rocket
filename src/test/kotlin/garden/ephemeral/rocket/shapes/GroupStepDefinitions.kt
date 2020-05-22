package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.java8.En

class GroupStepDefinitions: En {
    init {
        ParameterType("group", "group\\(\\)") { _ -> Group() }

        Given("{shape_var} ← {group}") { sv: String, g: Group ->
            shapes[sv] = g
        }

        When("add_child\\({shape_var}, {shape_var})") { sv1: String, sv2: String ->
            (shapes[sv1] as Group).addChild(shapes[sv2]!!)
        }

        When("{shape_var} ← first child of {shape_var}") { sv1: String, sv2: String ->
            shapes[sv1] = (shapes[sv2] as Group).children[0]
        }
        When("{shape_var} ← second child of {shape_var}") { sv1: String, sv2: String ->
            shapes[sv1] = (shapes[sv2] as Group).children[1]
            println("just set shapes[$sv1] to ${shapes[sv1]}")
        }
        When("{shape_var} ← third child of {shape_var}") { sv1: String, sv2: String ->
            shapes[sv1] = (shapes[sv2] as Group).children[2]
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