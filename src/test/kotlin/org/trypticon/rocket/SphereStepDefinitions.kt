package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.trypticon.rocket.CommonParameterTypes.Companion.realFromString
import org.trypticon.rocket.MaterialStepDefinitions.Companion.m
import org.trypticon.rocket.MatrixStepDefinitions.Companion.matrices
import org.trypticon.rocket.MatrixStepDefinitions.Companion.scalingFromString
import org.trypticon.rocket.Tuple.Companion.color
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples

class SphereStepDefinitions: En {
    companion object {
        val spheres: MutableMap<String, Sphere> = mutableMapOf()
    }

    init {
        val sphereValRegex = "s\\d*|shape|outer|inner"
        ParameterType("sphere_var", sphereValRegex) { string -> string }

        Given("^($sphereValRegex) ← sphere\\(\\)") { sv: String ->
            spheres[sv] = Sphere()
        }

        Given("^($sphereValRegex) ← sphere\\(\\) with:") { sv: String, dataTable: DataTable ->
            spheres[sv] = Sphere().apply {
                dataTable.asLists().forEach { row ->
                    when {
                        row[0] == "material.color" -> {
                            val params = row[1].substring(1, row[1].length - 1).split(", ")
                            material.color = color(
                                realFromString(params[0]), realFromString(params[1]),
                                realFromString(params[2])
                            )
                        }
                        row[0] == "material.diffuse" -> {
                            material.diffuse = realFromString(row[1])
                        }
                        row[0] == "material.specular" -> {
                            material.specular = realFromString(row[1])
                        }
                        row[0] == "transform" -> {
                            transform = scalingFromString(row[1])
                        }
                        else -> {
                            throw IllegalArgumentException("Unrecognised row: $row")
                        }
                    }
                }
            }
        }

        Given("{sphere_var}.material.ambient ← {real}") { sv: String, v: Double ->
            spheres[sv]!!.material.ambient = v
        }

        When("set_transform\\({sphere_var}, {matrix_var})") { sv: String, mv: String ->
            spheres[sv]!!.transform = matrices[mv]!!
        }
        When("set_transform\\({sphere_var}, {translation})") { sv: String, m: Matrix ->
            spheres[sv]!!.transform = m
        }
        When("set_transform\\({sphere_var}, {scaling})") { sv: String, m: Matrix ->
            spheres[sv]!!.transform = m
        }

        When("{tuple_var} ← normal_at\\({sphere_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = spheres[sv]!!.worldNormalAt(p)
        }

        When("m ← {sphere_var}.material") { sv: String ->
            m = spheres[sv]!!.material
        }
        When("{sphere_var}.material ← m") { sv: String ->
            spheres[sv]!!.material = m
        }

        Then("{sphere_var}.transform = {matrix_var}") { sv: String, mv: String ->
            assertThat(spheres[sv]!!.transform).isEqualTo(matrices[mv])
        }

        Then("{sphere_var}.material = m") { sv: String ->
            assertThat(spheres[sv]!!.material).isEqualTo(m)
        }
        Then("{tuple_var} = {sphere_var}.material.color") { tv: String, sv: String ->
            assertThat(tuples[tv]!!).isEqualTo(spheres[sv]!!.material.color)
        }
    }
}