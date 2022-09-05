package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Universe
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class PlaneStepDefinitions(universe: Universe) : En {
    init {
        ParameterType("plane", "(?:plane)\\(\\)") { _ -> Plane() }

        Given("{shape_var} ← {plane}") { sv: String, s: Shape ->
            universe.shapes[sv] = s
        }

        Given("{shape_var} ← {plane} with:") { sv: String, s: Shape, dataTable: DataTable ->
            universe.shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}
