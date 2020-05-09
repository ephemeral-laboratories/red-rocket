
import TupleStepDefinitions.Companion.tuples
import assertk.Assert
import assertk.assertThat
import assertk.assertions.*
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class MatrixStepDefinitions: En {
    private val epsilon: Double = 0.00001
    private val theFollowingMatrix = "the following( 4x4)( 3x3)( 2x2) matrix"
    private val matrices : MutableMap<String, Matrix> = mutableMapOf()

    init {
        matrices["I"] = Matrix.identity4x4

        Given("$theFollowingMatrix {mvar}:") { v: String, dataTable: DataTable ->
            matrices[v] = matrixFromDataTable(dataTable)
        }
        Given("{mvar} ← {mvar} * {mvar}") { v1: String, v2: String, v3: String ->
            matrices[v1] = matrices[v2]!! * matrices[v3]!!
        }
        Given("{mvar} ← transpose\\({mvar})") { v1: String, v2: String ->
            matrices[v1] = matrices[v2]!!.transpose()
        }
        Given("{mvar} ← inverse\\({mvar}).") { v1: String, v2: String ->
            matrices[v1] = matrices[v2]!!.inverse()
        }
        Given("{mvar} ← submatrix\\({mvar}, {int}, {int})") { v1: String, v2: String, row: Int, column: Int ->
            matrices[v1] = matrices[v2]!!.submatrix(row, column)
        }


        Then("{mvar}[{int}, {int}] = {real}") { v: String, r: Int, c: Int, e: Double ->
            assertThat(matrices[v]!!.getCell(r, c)).isEqualTo(e)
        }

        Then("transpose\\({mvar}) is $theFollowingMatrix:") { v: String, dataTable: DataTable ->
            assertThat(matrices[v]!!.transpose()).isEqualTo(matrixFromDataTable(dataTable))
        }

        Then("{mvar} = {mvar}") { v1: String?, v2: String? -> assertThat(matrices[v1]!!).isEqualTo(matrices[v2]!!) }
        Then("{mvar} != {mvar}") { v1: String?, v2: String? -> assertThat(matrices[v1]!!).isNotEqualTo(matrices[v2]!!) }

        Then("{mvar} is $theFollowingMatrix:") { v: String, dataTable: DataTable ->
            assertThat(matrices[v]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }
        Then("{mvar} * {mvar} is $theFollowingMatrix:") { v1: String?, v2: String, dataTable: DataTable ->
            assertThat(matrices[v1]!! * matrices[v2]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }

        Then("{mvar} * {mvar} = {mvar}") { v1: String, v2: String, v3: String ->
            assertThat(matrices[v1]!! * matrices[v2]!!).isEqualTo(matrices[v3]!!)
        }
        Then("{mvar} * {var} = {var}") { v1: String, v2: String, v3: String ->
            assertThat(matrices[v1]!! * tuples[v2]!!).isEqualTo(tuples[v3]!!)
        }
        Then("{mvar} * {var} = {tuple}") { v1: String, v2: String, e: Tuple ->
            assertThat(matrices[v1]!! * tuples[v2]!!).isEqualTo(e)
        }

        Then("determinant\\({mvar}) = {real}") { v: String, e: Double ->
            assertThat(matrices[v]!!.determinant).isEqualTo(e)
        }

        Then("{mvar} * inverse\\({mvar}) = {mvar}") { v1: String, v2: String, v3: String ->
            assertThat(matrices[v1]!! * matrices[v2]!!.inverse()).isCloseTo(matrices[v3]!!, epsilon)
        }

        Then("submatrix\\({mvar}, {int}, {int}) is $theFollowingMatrix:") {
                v: String, row: Int, column: Int, dataTable: DataTable ->
            assertThat(matrices[v]!!.submatrix(row, column)).isEqualTo(matrixFromDataTable(dataTable))
        }
        Then("minor\\({mvar}, {int}, {int}) = {real}") { v: String, row: Int, column: Int, e: Double ->
            assertThat(matrices[v]!!.minor(row, column)).isEqualTo(e)
        }
        Then("cofactor\\({mvar}, {int}, {int}) = {real}") { v: String, row: Int, column: Int, e: Double ->
            assertThat(matrices[v]!!.cofactor(row, column)).isEqualTo(e)
        }
        Then("{mvar} is invertible") { v: String ->
            assertThat(matrices[v]!!.invertible).isTrue()
        }
        Then("{mvar} is not invertible") { v: String ->
            assertThat(matrices[v]!!.invertible).isFalse()
        }
        Then("inverse\\({mvar}) is $theFollowingMatrix:") { v: String, dataTable: DataTable ->
            assertThat(matrices[v]!!.inverse()).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }

    }

    fun matrixFromDataTable(dataTable: DataTable): Matrix {
        return Matrix.fromLists(dataTable.asLists(Double::class.java))
    }
}

private fun Assert<Matrix>.isCloseTo(expected: Matrix, epsilon: Double) {
    matchesPredicate { m : Matrix -> m.isCloseTo(expected, epsilon) }
}
