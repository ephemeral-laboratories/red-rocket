package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.LUDecomposition
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

    private val luDecomposition by lazy { LUDecomposition(this) }

    val determinant: Double by lazy { luDecomposition.determinant }

    val inverse: Matrix by lazy { luDecomposition.inverse }

    val isInvertible: Boolean get() = luDecomposition.isNonSingular

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

    fun getCell(rowIndex: Int, columnIndex: Int): Double {
        return cells[rowIndex * columnCount + columnIndex]
    }

    fun getRow(rowIndex: Int): DoubleVector {
        return DoubleVector.fromArray(DoubleVector.SPECIES_256, cells, rowIndex * columnCount)
    }

    fun toArrays(): Array<DoubleArray> {
        return Array(rowCount) { row -> DoubleArray(columnCount) { col -> getCell(row, col) } }
    }

    fun toLists(): List<List<Double>> {
        return toArrays().map { row -> row.toList() }
    }

    fun transpose(): Matrix {
        val transposedCells = DoubleArray(cells.size)
        (0 until rowCount).forEach { rowIndex: Int ->
            (0 until columnCount).forEach { columnIndex: Int ->
                transposedCells[columnIndex * rowCount + rowIndex] = getCell(rowIndex, columnIndex)
            }
        }
        return Matrix(columnCount, rowCount, transposedCells)
    }

    operator fun times(scalar: Double): Matrix {
        val newCells = DoubleArray(cells.size)
        cells.forEachIndexed { index, cell -> newCells[index] = cell * scalar }
        return Matrix(rowCount, columnCount, newCells)
    }

    operator fun times(their: Matrix): Matrix {
        if (columnCount != their.rowCount) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.rowCount}")
        }

        val resultCells = DoubleArray(rowCount * their.columnCount)
        (0 until rowCount).forEach { rowIndex ->
            (0 until their.columnCount).forEach { columnIndex ->
                resultCells[rowIndex * their.columnCount + columnIndex] = (0 until columnCount).sumOf { index ->
                    getCell(rowIndex, index) * their.getCell(index, columnIndex)
                }
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
        for (rowIndex in 0 until rowCount) {
            result[rowIndex] = (0 until columnCount).sumOf { index ->
                getCell(rowIndex, index) * their[index]
            }
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
            result[rowIndex] = getRow(rowIndex).mul(their).reduceLanes(VectorOperators.ADD)
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
