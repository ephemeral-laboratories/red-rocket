package garden.ephemeral.rocket

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import assertk.assertions.matchesPredicate
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Transforms.Companion.rotationX
import garden.ephemeral.rocket.Transforms.Companion.rotationY
import garden.ephemeral.rocket.Transforms.Companion.rotationZ
import garden.ephemeral.rocket.Transforms.Companion.scaling
import garden.ephemeral.rocket.Transforms.Companion.shearing
import garden.ephemeral.rocket.Transforms.Companion.translation
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import garden.ephemeral.rocket.util.rad
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlin.math.abs

class MatrixStepDefinitions : En {
    private val theFollowingMatrix = "the following( 4x4)( 3x3)( 2x2) matrix"

    companion object {
        val matrices = mutableMapOf<String, Matrix>()

        fun transformFromString(string: String): Matrix {
            val open = string.indexOf('(')
            val close = string.indexOf(')')
            val params = string.substring(open + 1, close).split(", ")
            return when (string.substring(0, open)) {
                "translation" -> {
                    translation(realFromString(params[0]), realFromString(params[1]), realFromString(params[2]))
                }
                "scaling" -> {
                    scaling(realFromString(params[0]), realFromString(params[1]), realFromString(params[2]))
                }
                "rotation_x" -> { rotationX(realFromString(params[0]).rad) }
                "rotation_y" -> { rotationY(realFromString(params[0]).rad) }
                "rotation_z" -> { rotationZ(realFromString(params[0]).rad) }
                else -> {
                    throw IllegalArgumentException("Unknown transform: $string")
                }
            }
        }
    }

    init {
        matrices["identity_matrix"] = Matrix.identity4x4

        ParameterType("matrix_var", "(A|B|C|m|M|t|T|identity_matrix|transform|inv|half_quarter|full_quarter)\\d?") {
                string ->
            string
        }

        ParameterType("transform", "(?:translation|scaling|rotation_[xyz])\\((?:(?:$realRegex)(?:, )?)+\\)") { string ->
            transformFromString(string)
        }

        Given("$theFollowingMatrix {matrix_var}:") { mv: String, dataTable: DataTable ->
            matrices[mv] = matrixFromDataTable(dataTable)
        }
        Given("{matrix_var} ← {matrix_var} * {matrix_var}") { mv1: String, mv2: String, mv3: String ->
            matrices[mv1] = matrices[mv2]!! * matrices[mv3]!!
        }
        Given("{tuple_var} ← {matrix_var} * {tuple_var}") { tv1: String, mv: String, tv2: String ->
            tuples[tv1] = matrices[mv]!! * tuples[tv2]!!
        }
        Given("{matrix_var} ← transpose\\({matrix_var})") { mv1: String, mv2: String ->
            matrices[mv1] = matrices[mv2]!!.transpose()
        }
        Given("{matrix_var} ← inverse\\({matrix_var})(.)") { mv1: String, mv2: String ->
            matrices[mv1] = matrices[mv2]!!.inverse
        }
        Given("{matrix_var} ← {transform}") { mv: String, m: Matrix ->
            matrices[mv] = m
        }
        Given("{matrix_var} ← shearing\\({real}, {real}, {real}, {real}, {real}, {real})") {
                mv: String, xy: Double, xz: Double, yx: Double, yz: Double, zx: Double, zy: Double ->
            matrices[mv] = shearing(xy, xz, yx, yz, zx, zy)
        }
        Given("{matrix_var} ← view_transform\\({tuple_var}, {tuple_var}, {tuple_var})") {
                mv: String, tv1: String, tv2: String, tv3: String ->
            matrices[mv] = viewTransform(tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!)
        }

        Given("{matrix_var} ← {transform} * {transform}") { mv: String, m1: Matrix, m2: Matrix ->
            matrices[mv] = m1 * m2
        }

        When("{matrix_var} ← {matrix_var} * {matrix_var} * {matrix_var}") {
                mv1: String, mv2: String, mv3: String, mv4: String ->
            matrices[mv1] = matrices[mv2]!! * matrices[mv3]!! * matrices[mv4]!!
        }

        Then("{matrix_var}[{int}, {int}] = {real}") { v: String, r: Int, c: Int, e: Double ->
            assertThat(matrices[v]!![r, c]).isCloseTo(e, epsilon)
        }

        Then("transpose\\({matrix_var}) is $theFollowingMatrix:") { mv: String, dataTable: DataTable ->
            assertThat(matrices[mv]!!.transpose()).isEqualTo(matrixFromDataTable(dataTable))
        }

        Then("{matrix_var} = {matrix_var}") { mv1: String?, mv2: String? ->
            assertThat(matrices[mv1]!!).isEqualTo(matrices[mv2]!!)
        }
        Then("{matrix_var} != {matrix_var}") { mv1: String?, mv2: String? ->
            assertThat(matrices[mv1]!!).isNotEqualTo(matrices[mv2]!!)
        }

        Given("{matrix_var} = {transform}") { mv: String, m: Matrix ->
            assertThat(matrices[mv]!!).isCloseTo(m, epsilon)
        }

        Then("{matrix_var} is $theFollowingMatrix:") { mv: String, dataTable: DataTable ->
            assertThat(matrices[mv]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }
        Then("{matrix_var} * {real} is $theFollowingMatrix:") { mv: String?, scalar: Double, dataTable: DataTable ->
            assertThat(matrices[mv]!! * scalar).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }
        Then("{matrix_var} * {matrix_var} is $theFollowingMatrix:") { mv1: String?, mv2: String, dataTable: DataTable ->
            assertThat(matrices[mv1]!! * matrices[mv2]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }

        Then("{matrix_var} * {matrix_var} = {matrix_var}") { mv1: String, mv2: String, v3: String ->
            assertThat(matrices[mv1]!! * matrices[mv2]!!).isCloseTo(matrices[v3]!!, epsilon)
        }
        Then("{matrix_var} * {tuple_var} = {tuple_var}") { mv: String, tv1: String, tv2: String ->
            assertThat(matrices[mv]!! * tuples[tv1]!!).isCloseTo(tuples[tv2]!!, epsilon)
        }
        Then("{matrix_var} * {tuple_var} = {tuple}") { mv: String, tv: String, e: Tuple ->
            assertThat(matrices[mv]!! * tuples[tv]!!).isCloseTo(e, epsilon)
        }
        Then("{matrix_var} * {tuple_var} = {point}") { mv: String, tv: String, e: Tuple ->
            assertThat(matrices[mv]!! * tuples[tv]!!).isCloseTo(e, epsilon)
        }
        Then("{matrix_var} * {tuple_var} = {vector}") { mv: String, tv: String, e: Tuple ->
            assertThat(matrices[mv]!! * tuples[tv]!!).isCloseTo(e, epsilon)
        }

        Then("determinant\\({matrix_var}) = {real}") { mv: String, e: Double ->
            assertThat(matrices[mv]!!.determinant).isCloseTo(e, epsilon)
        }

        Then("{matrix_var} * inverse\\({matrix_var}) = {matrix_var}") { mv1: String, mv2: String, mv3: String ->
            assertThat(matrices[mv1]!! * matrices[mv2]!!.inverse).isCloseTo(
                matrices[mv3]!!,
                epsilon
            )
        }

        Then("{matrix_var} is invertible") { mv: String ->
            assertThat(matrices[mv]!!.isInvertible).isTrue()
        }
        Then("{matrix_var} is not invertible") { mv: String ->
            assertThat(matrices[mv]!!.isInvertible).isFalse()
        }
        Then("inverse\\({matrix_var}) is $theFollowingMatrix:") { mv: String, dataTable: DataTable ->
            assertThat(matrices[mv]!!.inverse).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }
    }

    private fun matrixFromDataTable(dataTable: DataTable): Matrix {
        val lists = dataTable.asLists().map { row -> row.map { cell -> realFromString(cell) } }
        return Matrix.fromLists(lists)
    }
}

private fun Assert<Matrix>.isCloseTo(expected: Matrix, delta: Double) {
    matchesPredicate { m: Matrix ->
        if (!m.isCloseTo(expected, delta)) {
            System.err.println("FAIL: EXPECTED = $expected")
            System.err.println("FAIL: ACTUAL   = $m")
        }

        m.isCloseTo(expected, delta)
    }
}

fun Matrix.isCloseTo(their: Matrix, delta: Double): Boolean {
    if (their.rowCount != rowCount || their.columnCount != columnCount) {
        return false
    }

    for (rowIndex in 0 until rowCount) {
        for (columnIndex in 0 until columnCount) {
            if (abs(this[rowIndex, columnIndex] - their[rowIndex, columnIndex]) > delta) {
                return false
            }
        }
    }

    return true
}
