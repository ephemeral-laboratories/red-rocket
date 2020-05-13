package org.trypticon.rocket.shapes

import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class PlaneStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← plane\\(\\)") { sv: String -> shapes[sv] =
            Plane()
        }
    }
}