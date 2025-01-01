package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.LupDecomposition
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times

data class Matrix(val cells: D2Array<Double>) {
    private val lupDecomposition by lazy { LupDecomposition(this) }

    val determinant: Double by lazy { lupDecomposition.determinant }

    val inverse: Matrix by lazy { lupDecomposition.inverse }

    val isInvertible: Boolean get() = lupDecomposition.isNonSingular

    companion object {
        val identity2x2 = identityNxN(2)
        val identity3x3 = identityNxN(3)
        val identity4x4 = identityNxN(4)

        fun identityNxN(size: Int) = Matrix(mk.identity<Double>(size))

        fun fromLists(cells: List<List<Double>>) = Matrix(mk.ndarray(cells))

        fun fromArrays(cells: Array<DoubleArray>) = Matrix(mk.ndarray(cells))
    }

    operator fun get(rowIndex: Int, columnIndex: Int): Double {
        return cells[rowIndex, columnIndex]
    }

    fun getRow(rowIndex: Int): MultiArray<Double, D1> {
        return cells[0..cells.shape[0], rowIndex]
    }

    fun getRows(): List<MultiArray<Double, D1>> {
        return (0 until cells.shape[0]).map { rowIndex -> getRow(rowIndex) }
    }

    fun getColumn(columnIndex: Int): MultiArray<Double, D1> {
        return cells[columnIndex, 0..cells.shape[1]]
    }

    fun getColumns(): List<MultiArray<Double, D1>> {
        return (0 until cells.shape[1]).map { columnIndex -> getColumn(columnIndex) }
    }

    fun transpose(): Matrix {
        return Matrix(cells.transpose(1, 0))
    }

    operator fun plus(other: Matrix): Matrix {
        requireSameSize(other)
        return Matrix(cells + other.cells)
    }

    operator fun minus(other: Matrix): Matrix {
        requireSameSize(other)
        return Matrix(cells - other.cells)
    }

    operator fun times(scalar: Double): Matrix {
        return Matrix(cells * scalar)
    }

    operator fun times(their: Matrix): Matrix {
        return Matrix(cells dot their.cells)
    }

    operator fun times(their: Tuple): Tuple {
        return Tuple(cells dot their.cells)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix) return false

        return cells == other.cells
    }

    override fun hashCode(): Int {
        return cells.hashCode()
    }

    private fun requireSameSize(other: Matrix) {
        require(cells.shape.contentEquals(other.cells.shape)) {
            "Other matrix does not match the size of this matrix: " +
                "our shape = ${cells.shape.contentToString()}, " +
                "other shape = ${other.cells.shape.contentToString()}"
        }
    }
}
