package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.shapes.CylinderLikeShapeStepDefinitions.Companion.configureFromDataTable
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class CylinderStepDefinitions : En {
    init {
        ParameterType("cylinder", "cylinder\\(\\)") { _ -> Cylinder() }

        Given("{shape_var} ← {cylinder}") { sv: String, s: Shape ->
            shapes[sv] = s
        }
        Given("{shape_var} ← {cylinder} with:") { sv: String, s: CylinderLikeShape, dataTable: DataTable ->
            shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}
