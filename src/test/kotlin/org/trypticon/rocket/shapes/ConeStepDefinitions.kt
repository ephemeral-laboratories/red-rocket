package org.trypticon.rocket.shapes

import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class ConeStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← cone\\(\\)") { sv: String ->
            shapes[sv] = Cone()
        }
    }
}