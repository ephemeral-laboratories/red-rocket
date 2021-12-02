package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class CylinderLikeShapeStepDefinitions : En {
    companion object {
        fun configureFromDataTable(shape: CylinderLikeShape, dataTable: DataTable) {
            val unrecognisedRows = mutableListOf<List<String>>()
            dataTable.asLists().forEach { row ->
                when (row[0]) {
                    "minimum" -> { shape.minimum = realFromString(row[1]) }
                    "maximum" -> { shape.maximum = realFromString(row[1]) }
                    "closed" -> shape.isClosed = row[1] == "true"
                    else -> { unrecognisedRows.add(row) }
                }
            }
            ShapeStepDefinitions.configureFromDataTableRows(shape, unrecognisedRows)
        }
    }

    init {
        Given("{shape_var}.minimum ← {real}") { sv: String, v: Double ->
            (shapes[sv]!! as CylinderLikeShape).minimum = v
        }
        Given("{shape_var}.maximum ← {real}") { sv: String, v: Double ->
            (shapes[sv]!! as CylinderLikeShape).maximum = v
        }
        Given("{shape_var}.closed ← {boolean}") { sv: String, v: Boolean ->
            (shapes[sv]!! as CylinderLikeShape).isClosed = v
        }

        Then("{shape_var}.minimum = {real}") { sv: String, e: Double ->
            assertThat((shapes[sv]!! as CylinderLikeShape).minimum).isEqualTo(e)
        }
        Then("{shape_var}.maximum = {real}") { sv: String, e: Double ->
            assertThat((shapes[sv]!! as CylinderLikeShape).maximum).isEqualTo(e)
        }
        Then("{shape_var}.closed = {boolean}") { sv: String, e: Boolean ->
            assertThat((shapes[sv]!! as CylinderLikeShape).isClosed).isEqualTo(e)
        }
    }
}
