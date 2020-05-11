
import CommonParameterTypes.Companion.realFromString
import CommonParameterTypes.Companion.realRegex
import Transforms.Companion.rotationX
import Transforms.Companion.rotationY
import Transforms.Companion.rotationZ
import Transforms.Companion.scaling
import Transforms.Companion.shearing
import Transforms.Companion.translation
import TupleStepDefinitions.Companion.tuples
import assertk.Assert
import assertk.assertThat
import assertk.assertions.*
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class MatrixStepDefinitions: En {
    private val epsilon: Double = 0.00001
    private val theFollowingMatrix = "the following( 4x4)( 3x3)( 2x2) matrix"

    companion object {
        val matrices: MutableMap<String, Matrix> = mutableMapOf()
    }

    init {
        matrices["identity_matrix"] = Matrix.identity4x4

        ParameterType("matrix_var", "A|B|C|m|M|t|T|identity_matrix|transform|inv|half_quarter|full_quarter") {
                string ->
            string
        }

        ParameterType("translation", "translation\\(($realRegex), ($realRegex), ($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            translation(realFromString(s1), realFromString(s2), realFromString(s3))
        }
        ParameterType("scaling", "scaling\\(($realRegex), ($realRegex), ($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            scaling(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        Given("$theFollowingMatrix {matrix_var}:") { v: String, dataTable: DataTable ->
            matrices[v] = matrixFromDataTable(dataTable)
        }
        Given("{matrix_var} ← {matrix_var} * {matrix_var}") { v1: String, v2: String, v3: String ->
            matrices[v1] = matrices[v2]!! * matrices[v3]!!
        }
        Given("{tuple_var} ← {matrix_var} * {tuple_var}") { v1: String, v2: String, v3: String ->
            tuples[v1] = matrices[v2]!! * tuples[v3]!!
        }
        Given("{matrix_var} ← transpose\\({matrix_var})") { v1: String, v2: String ->
            matrices[v1] = matrices[v2]!!.transpose()
        }
        Given("{matrix_var} ← inverse\\({matrix_var})(.)") { v1: String, v2: String ->
            matrices[v1] = matrices[v2]!!.inverse()
        }
        Given("{matrix_var} ← submatrix\\({matrix_var}, {int}, {int})") {
                v1: String, v2: String, row: Int, column: Int ->
            matrices[v1] = matrices[v2]!!.submatrix(row, column)
        }
        Given("{matrix_var} ← {translation}") { v: String, m: Matrix ->
            matrices[v] = m
        }
        Given("{matrix_var} ← {scaling}") { v: String, m: Matrix ->
            matrices[v] = m
        }
        Given("{matrix_var} ← rotation_x\\({real})") { v: String, theta: Double ->
            matrices[v] = rotationX(theta)
        }
        Given("{matrix_var} ← rotation_y\\({real})") { v: String, theta: Double ->
            matrices[v] = rotationY(theta)
        }
        Given("{matrix_var} ← rotation_z\\({real})") { v: String, theta: Double ->
            matrices[v] = rotationZ(theta)
        }
        Given("{matrix_var} ← shearing\\({real}, {real}, {real}, {real}, {real}, {real})") {
                v: String, xy: Double, xz: Double, yx: Double, yz: Double, zx: Double, zy: Double ->
            matrices[v] = shearing(xy, xz, yx, yz, zx, zy)
        }

        When("{matrix_var} ← {matrix_var} * {matrix_var} * {matrix_var}") {
                v1: String, v2: String, v3: String, v4: String ->
            matrices[v1] = matrices[v2]!! * matrices[v3]!! * matrices[v4]!!
        }

        Then("{matrix_var}[{int}, {int}] = {real}") { v: String, r: Int, c: Int, e: Double ->
            assertThat(matrices[v]!!.getCell(r, c)).isEqualTo(e)
        }

        Then("transpose\\({matrix_var}) is $theFollowingMatrix:") { v: String, dataTable: DataTable ->
            assertThat(matrices[v]!!.transpose()).isEqualTo(matrixFromDataTable(dataTable))
        }

        Then("{matrix_var} = {matrix_var}") { v1: String?, v2: String? ->
            assertThat(matrices[v1]!!).isEqualTo(matrices[v2]!!)
        }
        Then("{matrix_var} != {matrix_var}") { v1: String?, v2: String? ->
            assertThat(matrices[v1]!!).isNotEqualTo(matrices[v2]!!)
        }

        Then("{matrix_var} is $theFollowingMatrix:") { v: String, dataTable: DataTable ->
            assertThat(matrices[v]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }
        Then("{matrix_var} * {matrix_var} is $theFollowingMatrix:") { v1: String?, v2: String, dataTable: DataTable ->
            assertThat(matrices[v1]!! * matrices[v2]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }

        Then("{matrix_var} * {matrix_var} = {matrix_var}") { v1: String, v2: String, v3: String ->
            assertThat(matrices[v1]!! * matrices[v2]!!).isEqualTo(matrices[v3]!!)
        }
        Then("{matrix_var} * {tuple_var} = {tuple_var}") { v1: String, v2: String, v3: String ->
            assertThat(matrices[v1]!! * tuples[v2]!!).isEqualTo(tuples[v3]!!)
        }
        Then("{matrix_var} * {tuple_var} = {tuple}") { v1: String, v2: String, e: Tuple ->
            assertThat(matrices[v1]!! * tuples[v2]!!).isCloseTo(e, epsilon)
        }
        Then("{matrix_var} * {tuple_var} = {point}") { v1: String, v2: String, e: Tuple ->
            assertThat(matrices[v1]!! * tuples[v2]!!).isCloseTo(e, epsilon)
        }
        Then("{matrix_var} * {tuple_var} = {vector}") { v1: String, v2: String, e: Tuple ->
            assertThat(matrices[v1]!! * tuples[v2]!!).isCloseTo(e, epsilon)
        }

        Then("determinant\\({matrix_var}) = {real}") { v: String, e: Double ->
            assertThat(matrices[v]!!.determinant).isEqualTo(e)
        }

        Then("{matrix_var} * inverse\\({matrix_var}) = {matrix_var}") { v1: String, v2: String, v3: String ->
            assertThat(matrices[v1]!! * matrices[v2]!!.inverse()).isCloseTo(matrices[v3]!!, epsilon)
        }

        Then("submatrix\\({matrix_var}, {int}, {int}) is $theFollowingMatrix:") {
                v: String, row: Int, column: Int, dataTable: DataTable ->
            assertThat(matrices[v]!!.submatrix(row, column)).isEqualTo(matrixFromDataTable(dataTable))
        }
        Then("minor\\({matrix_var}, {int}, {int}) = {real}") { v: String, row: Int, column: Int, e: Double ->
            assertThat(matrices[v]!!.minor(row, column)).isEqualTo(e)
        }
        Then("cofactor\\({matrix_var}, {int}, {int}) = {real}") { v: String, row: Int, column: Int, e: Double ->
            assertThat(matrices[v]!!.cofactor(row, column)).isEqualTo(e)
        }
        Then("{matrix_var} is invertible") { v: String ->
            assertThat(matrices[v]!!.invertible).isTrue()
        }
        Then("{matrix_var} is not invertible") { v: String ->
            assertThat(matrices[v]!!.invertible).isFalse()
        }
        Then("inverse\\({matrix_var}) is $theFollowingMatrix:") { v: String, dataTable: DataTable ->
            assertThat(matrices[v]!!.inverse()).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }

    }

    fun matrixFromDataTable(dataTable: DataTable): Matrix {
        return Matrix.fromLists(dataTable.asLists(Double::class.java))
    }
}

private fun Assert<Matrix>.isCloseTo(expected: Matrix, delta: Double) {
    matchesPredicate { m : Matrix -> m.isCloseTo(expected, delta) }
}
