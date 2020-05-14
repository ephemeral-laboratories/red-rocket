package org.trypticon.rocket.shapes

import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class CubeStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← cube\\(\\)") { sv: String ->
            shapes[sv] = Cube()
        }
    }
}