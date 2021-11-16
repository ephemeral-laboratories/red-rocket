package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.shapes.CylinderLikeShapeStepDefinitions.Companion.configureFromDataTable
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class ConeStepDefinitions : En {
    init {
        ParameterType("cone", "cone\\(\\)") { _ -> Cone() }

        Given("{shape_var} ← {cone}") { sv: String, s: Shape ->
            shapes[sv] = s
        }
        Given("{shape_var} ← {cone} with:") { sv: String, s: CylinderLikeShape, dataTable: DataTable ->
            shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }
}
