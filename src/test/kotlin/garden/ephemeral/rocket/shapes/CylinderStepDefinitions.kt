package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.java8.En

class CylinderStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← cylinder\\(\\)") { sv: String ->
            shapes[sv] = Cylinder()
        }
    }
}