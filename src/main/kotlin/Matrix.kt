package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.LupDecomposition
import garden.ephemeral.rocket.util.buildImmutableDoubleArray
import garden.ephemeral.rocket.util.toImmutableDoubleArray
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import java.util.*

data class Matrix(val rowCount: Int, val columnCount: Int, val cells: ImmutableDoubleArray) {
    init {
        if (cells.size != rowCount * columnCount) {
            throw IllegalArgumentException(
                "Wrong cell count ${cells.size} for matrix of size $rowCount x $columnCount"
            )
        }
    }

    private val lupDecomposition by lazy { LupDecomposition(this) }

    val determinant: Double by lazy { lupDecomposition.determinant }

    val inverse: Matrix by lazy { lupDecomposition.inverse }

    val isInvertible: Boolean get() = lupDecomposition.isNonSingular

    companion object {
        val identity2x2 = identityNxN(2)
        val identity3x3 = identityNxN(3)
        val identity4x4 = identityNxN(4)

        fun identityNxN(size: Int): Matrix {
            val cells = buildImmutableDoubleArray(size * size) {
                for (rowIndex in 0 until size) {
                    for (columnIndex in 0 until size) {
                        add(if (rowIndex == columnIndex) 1.0 else 0.0)
                    }
                }
            }
            return Matrix(size, size, cells)
        }

        fun fromLists(cells: List<List<Double>>): Matrix {
            return Matrix(cells.size, cells[0].size, cells.flatten().toImmutableDoubleArray())
        }

        fun fromArrays(cells: Array<DoubleArray>): Matrix {
            return fromLists(cells.map { row -> row.toList() })
        }
    }

    operator fun get(rowIndex: Int, columnIndex: Int): Double {
        return cells[rowIndex * columnCount + columnIndex]
    }

    fun getRow(rowIndex: Int): ImmutableDoubleArray {
        val start = rowIndex * columnCount
        val end = start + columnCount - 1
        return cells.slice(start..end)
    }

    fun getRows(): List<ImmutableDoubleArray> {
        return (0 until rowCount).map { rowIndex -> getRow(rowIndex) }
    }

    fun getColumn(columnIndex: Int): ImmutableDoubleArray {
        val progression = columnIndex..cells.lastIndex step columnCount
        return cells.slice(progression.toList())
    }

    fun getColumns(): List<ImmutableDoubleArray> {
        return (0 until columnCount).map { columnIndex -> getColumn(columnIndex) }
    }

    fun transpose(): Matrix {
        val transposedCells = buildImmutableDoubleArray(cells.size) {
            for (columnIndex in 0 until columnCount) {
                for (rowIndex in 0 until rowCount) {
                    add(this@Matrix[rowIndex, columnIndex])
                }
            }
        }
        return Matrix(columnCount, rowCount, transposedCells)
    }

    operator fun plus(other: Matrix): Matrix {
        requireSameSize(other)
        return Matrix(rowCount, columnCount, cells + other.cells)
    }

    operator fun minus(other: Matrix): Matrix {
        requireSameSize(other)
        return Matrix(rowCount, columnCount, cells - other.cells)
    }

    operator fun times(scalar: Double): Matrix {
        return Matrix(rowCount, columnCount, cells * scalar)
    }

    operator fun times(their: Matrix): Matrix {
        if (columnCount != their.rowCount) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.rowCount}")
        }

        val resultCells = buildImmutableDoubleArray(rowCount * their.columnCount) {
            val ourRows = getRows()
            val theirColumns = their.getColumns()
            ourRows.forEach { ourRow ->
                theirColumns.forEach { theirColumn ->
                    add(ourRow.dotProduct(theirColumn))
                }
            }
        }
        return Matrix(rowCount, their.columnCount, resultCells)
    }

    operator fun times(their: Tuple): Tuple {
        return Tuple(times(their.cells))
    }

    operator fun times(their: ImmutableDoubleArray): ImmutableDoubleArray {
        if (columnCount != their.size) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.size}")
        }

        return buildImmutableDoubleArray(rowCount) {
            getRows().forEach { ourRow ->
                add(ourRow.dotProduct(their))
            }
        }
    }

    operator fun times(their: DoubleVector): DoubleVector {
        if (columnCount != their.length()) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.length()}")
        }

        val result = DoubleArray(rowCount)
        for (rowIndex in 0 until rowCount) {
            result[rowIndex] = cells.sliceAsDoubleVector(rowIndex * columnCount)
                .mul(their).reduceLanes(VectorOperators.ADD)
        }
        return DoubleVector.fromArray(DoubleVector.SPECIES_256, result, 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix) return false

        return rowCount == other.rowCount &&
                columnCount == other.columnCount &&
                cells == other.cells
    }

    override fun hashCode(): Int {
        return Objects.hash(rowCount, columnCount, cells)
    }

    private fun requireSameSize(other: Matrix) {
        require(rowCount == other.rowCount && columnCount == other.columnCount) {
            "Other matrix does not match the size of this matrix: " +
                    "our size = $rowCount x $columnCount, " +
                    "other size = ${other.rowCount} x ${other.columnCount}"
        }
    }
}
