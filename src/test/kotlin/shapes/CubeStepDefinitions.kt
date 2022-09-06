package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Space
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CubeStepDefinitions(space: Space) : En {
    init {
        ParameterType("cube", "cube\\(\\)") { _ -> Cube() }

        Given("{shape_var} ← {cube}") { sv: String, s: Shape ->
            space.shapes[sv] = s
        }
    }
}
