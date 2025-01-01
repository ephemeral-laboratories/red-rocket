package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import garden.ephemeral.rocket.Space
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class GroupStepDefinitions(space: Space) : En {
    init {
        ParameterType("group", "group\\(\\)") { _ -> Group() }

        Given("{shape_var} ← {group}") { sv: String, g: Group ->
            space.shapes[sv] = g
        }

        When("add_child\\({shape_var}, {shape_var})") { sv1: String, sv2: String ->
            (space.shapes[sv1] as Group).addChild(space.shapes[sv2]!!)
        }

        When("{shape_var} ← first child of {shape_var}") { sv1: String, sv2: String ->
            space.shapes[sv1] = (space.shapes[sv2] as Group).children[0]
        }
        When("{shape_var} ← second child of {shape_var}") { sv1: String, sv2: String ->
            space.shapes[sv1] = (space.shapes[sv2] as Group).children[1]
        }
        When("{shape_var} ← third child of {shape_var}") { sv1: String, sv2: String ->
            space.shapes[sv1] = (space.shapes[sv2] as Group).children[2]
        }

        Then("{shape_var} is empty") { sv: String ->
            assertThat((space.shapes[sv] as Group).children).isEmpty()
        }
        Then("{shape_var} is not empty") { sv: String ->
            assertThat((space.shapes[sv] as Group).children).isNotEmpty()
        }
        Then("{shape_var} includes {shape_var}") { sv1: String, sv2: String ->
            assertThat((space.shapes[sv1] as Group).children).contains(space.shapes[sv2]!!)
        }
    }
}
