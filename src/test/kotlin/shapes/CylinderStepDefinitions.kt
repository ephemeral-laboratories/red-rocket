package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Universe
import garden.ephemeral.rocket.shapes.CylinderLikeShapeStepDefinitions.Companion.configureFromDataTable
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CylinderStepDefinitions(universe: Universe) : En {
    init {
        ParameterType("cylinder", "cylinder\\(\\)") { _ -> Cylinder() }

        Given("{shape_var} ← {cylinder}") { sv: String, s: Shape ->
            universe.shapes[sv] = s
        }
        Given("{shape_var} ← {cylinder} with:") { sv: String, s: CylinderLikeShape, dataTable: DataTable ->
            universe.shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}
