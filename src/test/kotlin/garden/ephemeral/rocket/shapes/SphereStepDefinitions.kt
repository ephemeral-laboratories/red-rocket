package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Material
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class SphereStepDefinitions: En {
    init {
        ParameterType("sphere", "sphere\\(\\)") { _ -> Sphere() }

        Given("{shape_var} ← {sphere}") { sv: String, s: Shape ->
            shapes[sv] = s
        }
        Given("^($shapeVarRegex) ← glass_sphere\\(\\)") { sv: String ->
            shapes[sv] = glassSphere()
        }

        Given("{shape_var} ← {sphere} with:") { sv: String, s: Shape, dataTable: DataTable ->
            shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
        Given("^($shapeVarRegex) ← glass_sphere\\(\\) with:") { sv: String, dataTable: DataTable ->
            shapes[sv] = glassSphere().apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }

    private fun glassSphere(): Sphere {
        return Sphere().apply {
            material = Material.build {
                transparency = 1.0
                refractiveIndex = 1.5
            }
        }
    }
}