package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class PlaneStepDefinitions: En {
    init {
        ParameterType("plane", "(?:plane)\\(\\)") { _ -> Plane() }

        Given("{shape_var} ← {plane}") { sv: String, s: Shape ->
            shapes[sv] = s
        }

        Given("{shape_var} ← {plane} with:") { sv: String, s: Shape, dataTable: DataTable ->
            shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}