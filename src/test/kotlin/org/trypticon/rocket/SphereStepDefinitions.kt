package org.trypticon.rocket

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.trypticon.rocket.CommonParameterTypes.Companion.realFromString
import org.trypticon.rocket.MatrixStepDefinitions.Companion.transformFromString
import org.trypticon.rocket.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.ShapeStepDefinitions.Companion.shapes
import org.trypticon.rocket.Tuple.Companion.color

class SphereStepDefinitions: En {
    init {
        Given("^($shapeVarRegex) ← sphere\\(\\)") { sv: String ->
            shapes[sv] = Sphere()
        }

        Given("^($shapeVarRegex) ← sphere\\(\\) with:") { sv: String, dataTable: DataTable ->
            shapes[sv] = Sphere().apply {
                dataTable.asLists().forEach { row ->
                    when {
                        row[0] == "material.color" -> {
                            val params = row[1].substring(1, row[1].length - 1).split(", ")
                            material.color = color(
                                realFromString(params[0]), realFromString(params[1]),
                                realFromString(params[2])
                            )
                        }
                        row[0] == "material.diffuse" -> {
                            material.diffuse = realFromString(row[1])
                        }
                        row[0] == "material.specular" -> {
                            material.specular = realFromString(row[1])
                        }
                        row[0] == "transform" -> {
                            transform = transformFromString(row[1])
                        }
                        else -> {
                            throw IllegalArgumentException("Unrecognised row: $row")
                        }
                    }
                }
            }
        }
    }
}