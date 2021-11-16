package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.java8.En

class CubeStepDefinitions : En {
    init {
        ParameterType("cube", "cube\\(\\)") { _ -> Cube() }

        Given("{shape_var} ← {cube}") { sv: String, s: Shape ->
            shapes[sv] = s
        }
    }
}
