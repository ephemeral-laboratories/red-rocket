package org.trypticon.rocket.shapes

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class PlaneStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← plane\\(\\)") { sv: String ->
            shapes[sv] = Plane()
        }

        Given("^($shapeVarRegex) ← plane\\(\\) with:") { sv: String, dataTable: DataTable ->
            shapes[sv] = Plane().apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}