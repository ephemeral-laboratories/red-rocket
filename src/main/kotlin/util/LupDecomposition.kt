package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.util.LupDecomposition.NotSquareMatrixException
import kotlin.math.abs

/**
 * Calculates the LUP-decomposition of a square matrix.
 *
 * @param matrix the input matrix.
 * @param singularityThreshold a small value, smaller values get treated as if zero
 *        for checking whether the matrix is singular, i.e. non-invertible.
 * @throws NotSquareMatrixException if the matrix is not square.
 * @see [Wikipedia](http://en.wikipedia.org/wiki/LU_decomposition)
 */
class LupDecomposition(matrix: Matrix, singularityThreshold: Double = DEFAULT_TOO_SMALL) {
    /** Entries of LU decomposition.  */
    private val lu: Array<DoubleArray>

    /** Pivot permutation associated with LU decomposition.  */
    private val pivot: IntArray

    /** Parity of the permutation.  */
    private val even: Boolean

    /** Singularity indicator.  */
    val isSingular: Boolean
    val isNonSingular: Boolean
        get() = !isSingular

    /**
     * The determinant of the matrix.
     *
     * 0.0 if the matrix is singular.
     * Otherwise, computed by taking the product of the diagonal, negated if parity requires it.
     */
    val determinant: Double
        get() = if (isSingular) {
            0.0
        } else {
            val m = pivot.size
            (0 until m).fold(if (even) 1.0 else -1.0) { acc, i -> acc * lu[i][i] }
        }

    /**
     * The inverse of the matrix.
     *
     * @throws SingularMatrixException if the decomposed matrix is singular.
     */
    val inverse: Matrix
        get() = solve(Matrix.identityNxN(pivot.size))

    init {
        if (matrix.rowCount != matrix.columnCount) {
            throw NotSquareMatrixException("Matrix is not square! Got: $matrix")
        }

        val m = matrix.rowCount
        lu = Array(m) { row -> DoubleArray(m) { col -> matrix[row, col] } }

        // Initialize permutation array and parity
        pivot = IntArray(m) { row -> row }
        var isSingular = false
        var even = true

        // Loop over columns
        for (col in 0 until m) {

            // upper
            for (row in 0 until col) {
                val luRow = lu[row]
                var sum = luRow[col]
                for (i in 0 until row) {
                    sum -= luRow[i] * lu[i][col]
                }
                luRow[col] = sum
            }

            // lower
            var max = col // permutation row
            var largest = Double.NEGATIVE_INFINITY
            for (row in col until m) {
                val luRow = lu[row]
                var sum = luRow[col]
                for (i in 0 until col) {
                    sum -= luRow[i] * lu[i][col]
                }
                luRow[col] = sum

                // maintain best permutation choice
                if (abs(sum) > largest) {
                    largest = abs(sum)
                    max = row
                }
            }

            // Singularity check
            if (abs(lu[max][col]) < singularityThreshold) {
                isSingular = true
            } else {
                // Pivot if necessary
                if (max != col) {
                    var tmp: Double
                    val luMax = lu[max]
                    val luCol = lu[col]
                    for (i in 0 until m) {
                        tmp = luMax[i]
                        luMax[i] = luCol[i]
                        luCol[i] = tmp
                    }
                    val temp = pivot[max]
                    pivot[max] = pivot[col]
                    pivot[col] = temp
                    even = !even
                }

                // Divide the lower elements by the "winning" diagonal elt.
                val luDiag = lu[col][col]
                for (row in col + 1 until m) {
                    lu[row][col] /= luDiag
                }
            }
        }

        this.isSingular = isSingular
        this.even = even
    }

    fun solve(b: DoubleArray): DoubleArray {
        val m = pivot.size
        if (b.size != m) {
            throw WrongSizeException("Required vector of size $m! Got: $b")
        }
        if (isSingular) {
            throw SingularMatrixException()
        }

        // Apply permutations to b
        val bp = DoubleArray(m) { row -> b[pivot[row]] }

        // Solve LY = b
        for (col in 0 until m) {
            val bpCol = bp[col]
            for (i in col + 1 until m) {
                bp[i] -= bpCol * lu[i][col]
            }
        }

        // Solve UX = Y
        for (col in m - 1 downTo 0) {
            bp[col] /= lu[col][col]
            val bpCol = bp[col]
            for (i in 0 until col) {
                bp[i] -= bpCol * lu[i][col]
            }
        }
        return bp
    }

    fun solve(b: Matrix): Matrix {
        val m = pivot.size
        if (b.rowCount != m) {
            throw WrongSizeException("Required matrix with row count $m! Got: $b")
        }
        if (isSingular) {
            throw SingularMatrixException()
        }

        val nColB = b.columnCount

        // Apply permutations to b
        val bp = Array(m) { row ->
            val pRow = pivot[row]
            DoubleArray(nColB) { col -> b[pRow, col] }
        }

        // Solve LY = b
        for (col in 0 until m) {
            val bpCol = bp[col]
            for (i in col + 1 until m) {
                val bpI = bp[i]
                val luICol = lu[i][col]
                for (j in 0 until nColB) {
                    bpI[j] -= bpCol[j] * luICol
                }
            }
        }

        // Solve UX = Y
        for (col in m - 1 downTo 0) {
            val bpCol = bp[col]
            val luDiag = lu[col][col]
            for (j in 0 until nColB) {
                bpCol[j] /= luDiag
            }
            for (i in 0 until col) {
                val bpI = bp[i]
                val luICol = lu[i][col]
                for (j in 0 until nColB) {
                    bpI[j] -= bpCol[j] * luICol
                }
            }
        }

        return Matrix.fromLists(bp.map { row -> row.toList() })
    }

    companion object {
        /** Default bound to determine effective singularity in LU decomposition.
         * 1e-11
         * */
        private const val DEFAULT_TOO_SMALL = 1e-23
    }

    class WrongSizeException(message: String) : IllegalArgumentException(message)
    class NotSquareMatrixException(message: String) : IllegalArgumentException(message)
    class SingularMatrixException : UnsupportedOperationException("Matrix was singular")
}
