package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CylinderLikeShapeStepDefinitions(space: Space) : En {
    init {
        Given("{shape_var}.minimum ← {real}") { sv: String, v: Double ->
            (space.shapes[sv]!! as CylinderLikeShape).minimum = v
        }
        Given("{shape_var}.maximum ← {real}") { sv: String, v: Double ->
            (space.shapes[sv]!! as CylinderLikeShape).maximum = v
        }
        Given("{shape_var}.closed ← {boolean}") { sv: String, v: Boolean ->
            (space.shapes[sv]!! as CylinderLikeShape).isClosed = v
        }

        Then("{shape_var}.minimum = {real}") { sv: String, e: Double ->
            assertThat((space.shapes[sv]!! as CylinderLikeShape).minimum).isEqualTo(e)
        }
        Then("{shape_var}.maximum = {real}") { sv: String, e: Double ->
            assertThat((space.shapes[sv]!! as CylinderLikeShape).maximum).isEqualTo(e)
        }
        Then("{shape_var}.closed = {boolean}") { sv: String, e: Boolean ->
            assertThat((space.shapes[sv]!! as CylinderLikeShape).isClosed).isEqualTo(e)
        }
    }

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
}
