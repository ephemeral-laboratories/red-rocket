package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.color.Color.Companion.white
import garden.ephemeral.rocket.Material
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class SphereStepDefinitions: En {
    init {
        ParameterType("sphere", "(?:sphere|glass_sphere)\\(\\)") { string ->
            when (string.substring(0, string.indexOf('('))) {
                "sphere" -> Sphere()
                "glass_sphere" -> glassSphere()
                else -> throw IllegalArgumentException("Unknown sphere: $string")
            }
        }

        Given("{shape_var} ← {sphere}") { sv: String, s: Shape ->
            shapes[sv] = s
        }

        Given("{shape_var} ← {sphere} with:") { sv: String, s: Shape, dataTable: DataTable ->
            shapes[sv] = s.apply {
                configureFromDataTable(this, dataTable)
            }
        }
    }

    private fun glassSphere(): Sphere {
        return Sphere().apply {
            material = Material.build {
                transparency = white
                refractiveIndex = 1.5
            }
        }
    }
}