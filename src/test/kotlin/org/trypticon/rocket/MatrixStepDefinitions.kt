package org.trypticon.rocket

import assertk.Assert
import assertk.assertThat
import assertk.assertions.*
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.trypticon.rocket.CommonParameterTypes.Companion.epsilon
import org.trypticon.rocket.CommonParameterTypes.Companion.realFromString
import org.trypticon.rocket.CommonParameterTypes.Companion.realRegex
import org.trypticon.rocket.Transforms.Companion.rotationX
import org.trypticon.rocket.Transforms.Companion.rotationY
import org.trypticon.rocket.Transforms.Companion.rotationZ
import org.trypticon.rocket.Transforms.Companion.scaling
import org.trypticon.rocket.Transforms.Companion.shearing
import org.trypticon.rocket.Transforms.Companion.translation
import org.trypticon.rocket.Transforms.Companion.viewTransform
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples

class MatrixStepDefinitions: En {
    private val theFollowingMatrix = "the following( 4x4)( 3x3)( 2x2) matrix"

    companion object {
        val matrices: MutableMap<String, Matrix> = mutableMapOf()

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
                else -> {
                    throw IllegalArgumentException("Unknown transform: " + string)
                }
            }
        }
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
        ParameterType("rotation", "rotation_([xyz])\\(($realRegex)\\)") { s1: String, s2: String ->
            val theta = realFromString(s2)
            when (s1) {
                "x" -> { rotationX(theta) }
                "y" -> { rotationY(theta) }
                "z" -> { rotationZ(theta) }
                else -> { throw IllegalArgumentException("Unsupported rotation: ${s1}") }
            }
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
            matrices[mv1] = matrices[mv2]!!.inverse()
        }
        Given("{matrix_var} ← submatrix\\({matrix_var}, {int}, {int})") {
                mv1: String, mv2: String, row: Int, column: Int ->
            matrices[mv1] = matrices[mv2]!!.submatrix(row, column)
        }
        Given("{matrix_var} ← {translation}") { mv: String, m: Matrix ->
            matrices[mv] = m
        }
        Given("{matrix_var} ← {scaling}") { mv: String, m: Matrix ->
            matrices[mv] = m
        }
        Given("{matrix_var} ← rotation_x\\({real})") { mv: String, theta: Double ->
            matrices[mv] = rotationX(theta)
        }
        Given("{matrix_var} ← rotation_y\\({real})") { mv: String, theta: Double ->
            matrices[mv] = rotationY(theta)
        }
        Given("{matrix_var} ← rotation_z\\({real})") { mv: String, theta: Double ->
            matrices[mv] = rotationZ(theta)
        }
        Given("{matrix_var} ← shearing\\({real}, {real}, {real}, {real}, {real}, {real})") {
                mv: String, xy: Double, xz: Double, yx: Double, yz: Double, zx: Double, zy: Double ->
            matrices[mv] = shearing(xy, xz, yx, yz, zx, zy)
        }
        Given("{matrix_var} ← view_transform\\({tuple_var}, {tuple_var}, {tuple_var})") {
                mv: String, tv1: String, tv2: String, tv3: String ->
            matrices[mv] = viewTransform(tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!)
        }

        Given("{matrix_var} ← {scaling} * rotation_z\\({real})") { mv: String, m: Matrix, theta: Double ->
            matrices[mv] = m * rotationZ(theta)
        }

        When("{matrix_var} ← {matrix_var} * {matrix_var} * {matrix_var}") {
                mv1: String, mv2: String, mv3: String, mv4: String ->
            matrices[mv1] = matrices[mv2]!! * matrices[mv3]!! * matrices[mv4]!!
        }

        Then("{matrix_var}[{int}, {int}] = {real}") { v: String, r: Int, c: Int, e: Double ->
            assertThat(matrices[v]!!.getCell(r, c)).isEqualTo(e)
        }

        Then("transpose\\({matrix_var}) is $theFollowingMatrix:") { mv: String, dataTable: DataTable ->
            assertThat(matrices[mv]!!.transpose()).isEqualTo(matrixFromDataTable(dataTable))
        }

        Then("{matrix_var} = {matrix_var}") { mv1: String?, mv2: String? ->
            assertThat(matrices[mv1]!!).isEqualTo(
                matrices[mv2]!!)
        }
        Then("{matrix_var} != {matrix_var}") { mv1: String?, mv2: String? ->
            assertThat(matrices[mv1]!!).isNotEqualTo(
                matrices[mv2]!!)
        }

        Given("{matrix_var} = {translation}") { mv: String, m: Matrix ->
            assertThat(matrices[mv]!!).isEqualTo(m)
        }
        Given("{matrix_var} = {scaling}") { mv: String, m: Matrix ->
            assertThat(matrices[mv]!!).isEqualTo(m)
        }
        Given("{matrix_var} = {rotation}") { mv: String, m: Matrix ->
            assertThat(matrices[mv]!!).isEqualTo(m)
        }

        Then("{matrix_var} is $theFollowingMatrix:") { mv: String, dataTable: DataTable ->
            assertThat(matrices[mv]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }
        Then("{matrix_var} * {matrix_var} is $theFollowingMatrix:") { mv1: String?, mv2: String, dataTable: DataTable ->
            assertThat(matrices[mv1]!! * matrices[mv2]!!).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }

        Then("{matrix_var} * {matrix_var} = {matrix_var}") { mv1: String, mv2: String, v3: String ->
            assertThat(matrices[mv1]!! * matrices[mv2]!!).isEqualTo(
                matrices[v3]!!)
        }
        Then("{matrix_var} * {tuple_var} = {tuple_var}") { mv: String, tv1: String, tv2: String ->
            assertThat(matrices[mv]!! * tuples[tv1]!!).isEqualTo(tuples[tv2]!!)
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
            assertThat(matrices[mv]!!.determinant).isEqualTo(e)
        }

        Then("{matrix_var} * inverse\\({matrix_var}) = {matrix_var}") { mv1: String, mv2: String, mv3: String ->
            assertThat(matrices[mv1]!! * matrices[mv2]!!.inverse()).isCloseTo(
                matrices[mv3]!!, epsilon)
        }

        Then("submatrix\\({matrix_var}, {int}, {int}) is $theFollowingMatrix:") {
                mv: String, row: Int, column: Int, dataTable: DataTable ->
            assertThat(matrices[mv]!!.submatrix(row, column)).isEqualTo(matrixFromDataTable(dataTable))
        }
        Then("minor\\({matrix_var}, {int}, {int}) = {real}") { mv: String, row: Int, column: Int, e: Double ->
            assertThat(matrices[mv]!!.minor(row, column)).isEqualTo(e)
        }
        Then("cofactor\\({matrix_var}, {int}, {int}) = {real}") { mv: String, row: Int, column: Int, e: Double ->
            assertThat(matrices[mv]!!.cofactor(row, column)).isEqualTo(e)
        }
        Then("{matrix_var} is invertible") { mv: String ->
            assertThat(matrices[mv]!!.invertible).isTrue()
        }
        Then("{matrix_var} is not invertible") { mv: String ->
            assertThat(matrices[mv]!!.invertible).isFalse()
        }
        Then("inverse\\({matrix_var}) is $theFollowingMatrix:") { mv: String, dataTable: DataTable ->
            assertThat(matrices[mv]!!.inverse()).isCloseTo(matrixFromDataTable(dataTable), epsilon)
        }

    }

    fun matrixFromDataTable(dataTable: DataTable): Matrix {
        return Matrix.fromLists(dataTable.asLists(Double::class.java))
    }
}

private fun Assert<Matrix>.isCloseTo(expected: Matrix, delta: Double) {
    matchesPredicate { m : Matrix -> m.isCloseTo(expected, delta) }
}
