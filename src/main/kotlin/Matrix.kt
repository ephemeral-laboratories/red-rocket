package garden.ephemeral.rocket

data class Matrix(val rowCount: Int, val columnCount: Int, val cells: DoubleArray) {
    val determinant: Double by lazy {
        if (rowCount == 2 && columnCount == 2) {
            cells[0] * cells[3] - cells[1] * cells[2]
        } else {
            (0 until columnCount).sumOf { columnIndex -> getCell(0, columnIndex) * cofactor(0, columnIndex) }
        }
    }

    val inverse: Matrix by lazy {
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
        Matrix(columnCount, rowCount, inverseCells)
    }


    val invertible: Boolean
        get() = determinant != 0.0

    companion object {
        val identity2x2 = Matrix(2, 2, doubleArrayOf(
            1.0, 0.0,
            0.0, 1.0))
        val identity3x3 = Matrix(3, 3, doubleArrayOf(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0))
        val identity4x4 = Matrix(4, 4, doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0))

        fun fromLists(cells: List<List<Double>>): Matrix {
            return Matrix(
                cells.size,
                cells[0].size,
                cells.flatten().toDoubleArray()
            )
        }
    }

    fun getCell(rowIndex: Int, columnIndex: Int): Double {
        return cells[rowIndex * columnCount + columnIndex]
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
        (0 until rowCount).forEach { rowIndex ->
            (0 until their.columnCount).forEach { columnIndex ->
                resultCells[rowIndex * their.columnCount + columnIndex] = (0 until columnCount).sumByDouble { index ->
                    getCell(rowIndex, index) * their.getCell(index,columnIndex)
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
            result[rowIndex] = (0 until columnCount).sumByDouble { index ->
                getCell(rowIndex, index) * their[index]
            }
        }
        return result
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
