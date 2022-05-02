package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.DoubleArrays
import garden.ephemeral.rocket.util.LupDecomposition
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators

data class Matrix(val rowCount: Int, val columnCount: Int, val cells: DoubleArray) {
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
            val cells = DoubleArray(size * size)
            for (i in 0 until size) {
                cells[i * size + i] = 1.0
            }
            return Matrix(size, size, cells)
        }

        fun fromLists(cells: List<List<Double>>): Matrix {
            return Matrix(cells.size, cells[0].size, cells.flatten().toDoubleArray())
        }

        fun fromArrays(cells: Array<DoubleArray>): Matrix {
            return fromLists(cells.map { row -> row.toList() })
        }
    }

    operator fun get(rowIndex: Int, columnIndex: Int): Double {
        return cells[rowIndex * columnCount + columnIndex]
    }

    fun getRow(rowIndex: Int): DoubleArray {
        val start = rowIndex * columnCount
        val end = start + columnCount - 1
        return cells.sliceArray(start..end)
    }

    private fun getRowAsVector(rowIndex: Int): DoubleVector {
        return DoubleVector.fromArray(DoubleVector.SPECIES_256, cells, rowIndex * columnCount)
    }

    fun getRows(): List<DoubleArray> {
        return (0 until rowCount).map { rowIndex -> getRow(rowIndex) }
    }

    fun getColumn(columnIndex: Int): DoubleArray {
        val progression = columnIndex .. cells.lastIndex step columnCount
        return cells.sliceArray(progression.toList())
    }

    fun getColumns(): List<DoubleArray> {
        return (0 until columnCount).map { columnIndex -> getColumn(columnIndex) }
    }

    fun transpose(): Matrix {
        val transposedCells = DoubleArray(cells.size)
        (0 until rowCount).forEach { rowIndex: Int ->
            (0 until columnCount).forEach { columnIndex: Int ->
                transposedCells[columnIndex * rowCount + rowIndex] = this[rowIndex, columnIndex]
            }
        }
        return Matrix(columnCount, rowCount, transposedCells)
    }

    operator fun times(scalar: Double): Matrix {
        val newCells = DoubleArrays.multiply(cells, scalar)
        return Matrix(rowCount, columnCount, newCells)
    }

    operator fun times(their: Matrix): Matrix {
        if (columnCount != their.rowCount) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.rowCount}")
        }

        val resultCells = DoubleArray(rowCount * their.columnCount)
        val ourRows = getRows()
        val theirColumns = their.getColumns()
        var i = 0
        ourRows.forEach { ourRow ->
            theirColumns.forEach { theirColumn ->
                resultCells[i] = DoubleArrays.dotProduct(ourRow, theirColumn)
                i++
            }
        }
        return Matrix(rowCount, their.columnCount, resultCells)
    }

    operator fun times(their: Tuple): Tuple {
        return Tuple(times(their.cells))
    }

    operator fun times(their: DoubleArray): DoubleArray {
        if (columnCount != their.size) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.size}")
        }

        val result = DoubleArray(rowCount)
        var i = 0
        getRows().forEach { ourRow ->
            result[i] = DoubleArrays.dotProduct(ourRow, their)
            i++
        }
        return result
    }

    operator fun times(their: DoubleVector): DoubleVector {
        if (columnCount != their.length()) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.length()}")
        }

        // XXX: Can we get this as a vector?
        val result = DoubleArray(rowCount)
        for (rowIndex in 0 until rowCount) {
            result[rowIndex] = getRowAsVector(rowIndex).mul(their).reduceLanes(VectorOperators.ADD)
        }
        return DoubleVector.fromArray(DoubleVector.SPECIES_256, result, 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (rowCount != other.rowCount) return false
        if (columnCount != other.columnCount) return false
        if (!cells.contentEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rowCount
        result = 31 * result + columnCount
        result = 31 * result + cells.contentHashCode()
        return result
    }
}
