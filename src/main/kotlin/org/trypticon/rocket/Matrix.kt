package org.trypticon.rocket

import java.lang.Math.abs

data class Matrix(val rowCount: Int, val columnCount: Int, val cells: DoubleArray) {
    val determinant: Double by lazy {
        if (rowCount == 2 && columnCount == 2) {
            cells[0] * cells[3] - cells[1] * cells[2]
        } else {
            (0 until columnCount)
                .map { columnIndex -> getCell(0, columnIndex) * cofactor(0, columnIndex) }
                .sum()
        }
    }

    val invertible: Boolean
        get() = determinant != 0.0

    companion object {
        val identity2x2 = Matrix(2, 2, doubleArrayOf(1.0, 0.0, 0.0, 1.0))
        val identity3x3 = Matrix(
            3,
            3,
            doubleArrayOf(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
        )
        val identity4x4 = Matrix(
            4,
            4,
            doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        )

        fun fromLists(cells: List<List<Double>>): Matrix {
            return Matrix(
                cells.size,
                cells[0].size,
                cells.flatten().toDoubleArray()
            )
        }
    }

    fun getCell(rowIndex: Int, columnIndex: Int): Double {
        return cells[rowIndex * columnCount + columnIndex];
    }

    fun getRow(rowIndex: Int): Tuple {
        return Tuple(cells.sliceArray(rowIndex * columnCount until (rowIndex + 1) * columnCount))
    }

    fun getColumn(columnIndex: Int): Tuple {
        return Tuple(cells.sliceArray((0 until rowCount).map { row -> row * columnCount + columnIndex }))
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

    fun inverse(): Matrix {
        if (!invertible) {
            throw UnsupportedOperationException("This matrix is not invertible")
        }

        val inverseCells = DoubleArray(cells.size)
        (0 until rowCount).forEach { rowIndex ->
            (0 until columnCount).forEach { columnIndex ->
                val c = cofactor(rowIndex, columnIndex)
                inverseCells[columnIndex * rowCount + rowIndex] = c / determinant
            }
        }
        return Matrix(columnCount, rowCount, inverseCells)
    }

    fun submatrix(rowIndexToOmit: Int, columnIndexToOmit: Int): Matrix {
        if (rowCount == 1 || columnCount == 1) {
            throw IllegalStateException("We can't go any smaller")
        }

        val subRowCount = rowCount - 1
        val subColumnCount = columnCount - 1

        val subCells = DoubleArray(subRowCount * subColumnCount)
        (0 until rowCount).forEach { rowIndex: Int ->
            if (rowIndex != rowIndexToOmit) {
                val subRowIndex = if (rowIndex > rowIndexToOmit) {
                    rowIndex - 1
                } else {
                    rowIndex
                }

                (0 until columnCount).forEach { columnIndex: Int ->
                    if (columnIndex != columnIndexToOmit) {
                        val subColumnIndex = if (columnIndex > columnIndexToOmit) {
                            columnIndex - 1
                        } else {
                            columnIndex
                        }

                        subCells[subRowIndex * subRowCount + subColumnIndex] = getCell(rowIndex, columnIndex)
                    }
                }
            }
        }
        return Matrix(subRowCount, subColumnCount, subCells)
    }

    fun minor(row: Int, column: Int): Double {
        return submatrix(row, column).determinant
    }

    fun cofactor(row: Int, column: Int): Double {
        return minor(row, column) * if ((row + column) % 2 != 0) {
            -1.0
        } else {
            1.0
        }
    }

    operator fun times(their: Matrix): Matrix {
        if (columnCount != their.rowCount) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count ${their.rowCount}")
        }

        val resultCells = DoubleArray(rowCount * their.columnCount)
        for (rowIndex in 0 until rowCount) {
            for (columnIndex in 0 until their.columnCount) {
                resultCells[rowIndex * their.columnCount + columnIndex] = getRow(rowIndex).dot(their.getColumn(columnIndex))
            }
        }
        return Matrix(rowCount, their.columnCount, resultCells)
    }

    operator fun times(their: Tuple): Tuple {
        if (columnCount != 4) {
            throw IllegalArgumentException("Incompatible column count $columnCount with your row count 4")
        }

        val resultCells = DoubleArray(rowCount)
        for (rowIndex in 0 until rowCount) {
            resultCells[rowIndex] = getRow(rowIndex).dot(their)
        }
        return Tuple(resultCells)
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

    fun isCloseTo(their: Matrix, delta: Double): Boolean {
        if (their.rowCount != rowCount || their.columnCount != columnCount) {
            return false;
        }

        cells.forEachIndexed { index, value ->
            if (abs(their.cells[index] - value) > delta) {
                return false;
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = rowCount
        result = 31 * result + columnCount
        result = 31 * result + cells.contentHashCode()
        return result
    }
}
