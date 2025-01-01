package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class PlaneStepDefinitions(space: Space) : En {
    init {
        ParameterType("plane", "(?:plane)\\(\\)") { _ -> Plane() }

        Given("{shape_var} ← {plane}") { sv: String, s: Shape ->
            space.shapes[sv] = s
        }

        Given("{shape_var} ← {plane} with:") { sv: String, s: Shape, dataTable: DataTable ->
            space.shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}
