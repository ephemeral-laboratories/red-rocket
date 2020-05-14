package org.trypticon.rocket.shapes

import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class CylinderStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← cylinder\\(\\)") { sv: String ->
            shapes[sv] = Cylinder()
        }
    }
}