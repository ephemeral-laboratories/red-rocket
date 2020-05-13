package org.trypticon.rocket

import io.cucumber.java8.En
import org.trypticon.rocket.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.ShapeStepDefinitions.Companion.shapes

class PlaneStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← plane\\(\\)") { sv: String -> shapes[sv] = Plane() }
    }
}