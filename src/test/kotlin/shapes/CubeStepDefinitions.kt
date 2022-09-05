package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Universe
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CubeStepDefinitions(universe: Universe) : En {
    init {
        ParameterType("cube", "cube\\(\\)") { _ -> Cube() }

        Given("{shape_var} ← {cube}") { sv: String, s: Shape ->
            universe.shapes[sv] = s
        }
    }
}
