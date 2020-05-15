package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import garden.ephemeral.rocket.CommonParameterTypes.Companion.realFromString
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.IntersectionStepDefinitions.Companion.intersections
import garden.ephemeral.rocket.IntersectionStepDefinitions.Companion.xs
import garden.ephemeral.rocket.MaterialStepDefinitions.Companion.m
import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.MatrixStepDefinitions.Companion.matrices
import garden.ephemeral.rocket.MatrixStepDefinitions.Companion.transformFromString
import garden.ephemeral.rocket.RayStepDefinitions.Companion.rays
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.color
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.patterns.TestPattern
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class ShapeStepDefinitions: En {
    companion object {
        val shapes: MutableMap<String, Shape> = mutableMapOf()
        const val shapeVarRegex = "[gpst]\\d*|shape|outer|inner|lower|upper|object|floor|ball|cyl|tri\\d*"

        fun configureFromDataTable(shape: Shape, dataTable: DataTable) {
            dataTable.asLists().forEach { row ->
                when(row[0]) {
                    "material.color" -> {
                        val params = row[1].substring(1, row[1].length - 1).split(", ")
                        shape.material = shape.material.build {
                            color = Tuple.color(
                                realFromString(params[0]),
                                realFromString(params[1]),
                                realFromString(params[2])
                            )
                        }
                    }
                    "material.pattern" -> {
                        shape.material = shape.material.build {
                            pattern = if (row[1] == "test_pattern()") {
                                TestPattern()
                            } else {
                                throw IllegalArgumentException("Unrecognised row: $row")
                            }
                        }
                    }
                    "material.ambient" -> {
                        shape.material = shape.material.build {
                            ambient = realFromString(row[1])
                        }
                    }
                    "material.diffuse" -> {
                        shape.material = shape.material.build {
                            diffuse = realFromString(row[1])
                        }
                    }
                    "material.specular" -> {
                        shape.material = shape.material.build {
                            specular = realFromString(row[1])
                        }
                    }
                    "material.reflective" -> {
                        shape.material = shape.material.build {
                            reflective = realFromString(row[1])
                        }
                    }
                    "material.transparency" -> {
                        shape.material = shape.material.build {
                            transparency = realFromString(row[1])
                        }
                    }
                    "material.refractive_index" -> {
                        shape.material = shape.material.build {
                            refractiveIndex = realFromString(row[1])
                        }
                    }
                    "transform" -> {
                        shape.transform = transformFromString(row[1])
                    }
                    else -> {
                        throw IllegalArgumentException("Unrecognised row: $row")
                    }
                }
            }
        }
    }

    init {
        ParameterType("shape_var", shapeVarRegex) { string -> string }

        Given("^($shapeVarRegex) ← test_shape\\(\\)") { sv: String -> shapes[sv] = TestShape() }

        Given("{shape_var} has:") { sv: String, dataTable: DataTable ->
            configureFromDataTable(shapes[sv]!!, dataTable)
        }

        Given("{shape_var}.material.ambient ← {real}") { sv: String, v: Double ->
            shapes[sv]!!.material = shapes[sv]!!.material.build {
                ambient = v
            }
        }

        When("set_transform\\({shape_var}, {matrix_var})") { sv: String, mv: String ->
            shapes[sv]!!.transform = matrices[mv]!!
        }
        When("set_transform\\({shape_var}, {transform})") { sv: String, m: Matrix ->
            shapes[sv]!!.transform = m
        }

        When("{tuple_var} ← normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = shapes[sv]!!.worldNormalAt(p, dummyIntersection())
        }
        When("{tuple_var} ← normal_at\\({shape_var}, {point}, {intersection_var})") {
                tv: String, sv: String, p: Tuple, iv: String ->
            tuples[tv] = shapes[sv]!!.worldNormalAt(p, intersections[iv]!!)
        }

        When("m ← {shape_var}.material") { sv: String ->
            m = shapes[sv]!!.material
        }
        When("{shape_var}.material ← m") { sv: String ->
            shapes[sv]!!.material = m
        }

        When("xs ← local_intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            xs = shapes[sv]!!.localIntersect(rays[rv]!!)
        }
        When("{tuple_var} ← local_normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = shapes[sv]!!.localNormalAt(p, dummyIntersection())
        }
        When("{tuple_var} ← local_normal_at\\({shape_var}, {tuple_var})") { tv1: String, sv: String, tv2: String ->
            tuples[tv1] = shapes[sv]!!.localNormalAt(tuples[tv2]!!, dummyIntersection())
        }

        When("{tuple_var} ← world_to_object\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = shapes[sv]!!.worldToObject(p)
        }

        When("{tuple_var} ← normal_to_world\\({shape_var}, {vector})") { tv: String, sv: String, v: Tuple ->
            tuples[tv] = shapes[sv]!!.normalToWorld(v)
        }

        Then("{shape_var} = {shape_var}") { sv1: String, sv2: String ->
            assertThat(shapes[sv1]!!).isEqualTo(shapes[sv2]!!)
        }
        Then("{shape_var}.transform = {matrix_var}") { sv: String, mv: String ->
            assertThat(shapes[sv]!!.transform).isEqualTo(matrices[mv])
        }
        Then("{shape_var}.transform = {transform}") { sv: String, e: Matrix ->
            assertThat(shapes[sv]!!.transform).isEqualTo(e)
        }

        Then("{shape_var}.material = m") { sv: String ->
            assertThat(shapes[sv]!!.material).isEqualTo(m)
        }
        Then("{tuple_var} = {shape_var}.material.color") { tv: String, sv: String ->
            assertThat(tuples[tv]!!).isEqualTo(shapes[sv]!!.material.color)
        }
        Then("{shape_var}.material.transparency = {real}") { sv: String, e: Double ->
            assertThat(shapes[sv]!!.material.transparency).isEqualTo(e)
        }
        Then("{shape_var}.material.refractive_index = {real}") { sv: String, e: Double ->
            assertThat(shapes[sv]!!.material.refractiveIndex).isEqualTo(e)
        }

        Then("{shape_var}.saved_ray.origin = {point}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as TestShape).savedRay!!.origin).isEqualTo(e)
        }
        Then("{shape_var}.saved_ray.direction = {vector}") { sv: String, e: Tuple ->
            assertThat((shapes[sv] as TestShape).savedRay!!.direction).isEqualTo(e)
        }

        Then("{shape_var}.parent is nothing") { sv: String ->
            assertThat(shapes[sv]!!.parent).isNull()
        }
        Then("{shape_var}.parent = {shape_var}") { sv1: String, sv2: String ->
            assertThat(shapes[sv1]!!.parent).isEqualTo(shapes[sv2]!!)
        }
    }

    private fun dummyIntersection(): Intersection {
        return Intersection(0.0, Sphere(), 0.0, 0.0)
    }
}