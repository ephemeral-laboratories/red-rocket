package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

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