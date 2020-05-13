package org.trypticon.rocket.shapes

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.configureFromDataTable
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class SphereStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← sphere\\(\\)") { sv: String ->
            shapes[sv] = Sphere()
        }
        Given("^($shapeVarRegex) ← glass_sphere\\(\\)") { sv: String ->
            shapes[sv] = glassSphere()
        }

        Given("^($shapeVarRegex) ← sphere\\(\\) with:") { sv: String, dataTable: DataTable ->
            shapes[sv] = Sphere().apply {
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
            material.transparency = 1.0
            material.refractiveIndex = 1.5
        }
    }
}