package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.shapes.CylinderLikeShapeStepDefinitions.Companion.configureFromDataTable
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CylinderStepDefinitions(space: Space) : En {
    init {
        ParameterType("cylinder", "cylinder\\(\\)") { _ -> Cylinder() }

        Given("{shape_var} ← {cylinder}") { sv: String, s: Shape ->
            space.shapes[sv] = s
        }
        Given("{shape_var} ← {cylinder} with:") { sv: String, s: CylinderLikeShape, dataTable: DataTable ->
            space.shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}
