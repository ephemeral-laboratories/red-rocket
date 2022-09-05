package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Universe
import garden.ephemeral.rocket.shapes.CylinderLikeShapeStepDefinitions.Companion.configureFromDataTable
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class ConeStepDefinitions(universe: Universe) : En {
    init {
        ParameterType("cone", "cone\\(\\)") { _ -> Cone() }

        Given("{shape_var} ← {cone}") { sv: String, s: Shape ->
            universe.shapes[sv] = s
        }
        Given("{shape_var} ← {cone} with:") { sv: String, s: CylinderLikeShape, dataTable: DataTable ->
            universe.shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}
