package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.java8.En

class ConeStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← cone\\(\\)") { sv: String ->
            shapes[sv] = Cone()
        }
    }
}