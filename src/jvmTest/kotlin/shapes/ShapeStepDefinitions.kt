package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.MatrixStepDefinitions.Companion.transformFromString
import garden.ephemeral.rocket.Space
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import garden.ephemeral.rocket.patterns.TestPattern
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class ShapeStepDefinitions(space: Space) : En {
    init {
        ParameterType("shape_var", shapeVarRegex) { string -> string }

        Given("^($shapeVarRegex) ← test_shape\\(\\)") { sv: String -> space.shapes[sv] = TestShape() }

        Given("{shape_var} has:") { sv: String, dataTable: DataTable ->
            configureFromDataTable(space.shapes[sv]!!, dataTable)
        }

        Given("{shape_var}.material.ambient ← {real}") { sv: String, v: Double ->
            space.shapes[sv]!!.material = space.shapes[sv]!!.material.build {
                ambient = grey(v)
            }
        }

        When("set_transform\\({shape_var}, {matrix_var})") { sv: String, mv: String ->
            space.shapes[sv]!!.transform = space.matrices[mv]!!
        }
        When("set_transform\\({shape_var}, {transform})") { sv: String, transform: Matrix ->
            space.shapes[sv]!!.transform = transform
        }

        When("{tuple_var} ← normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            space.tuples[tv] = space.shapes[sv]!!.worldNormalAt(p, dummyIntersection())
        }
        When("{tuple_var} ← normal_at\\({shape_var}, {point}, {intersection_var})") {
                tv: String, sv: String, p: Tuple, iv: String ->
            space.tuples[tv] = space.shapes[sv]!!.worldNormalAt(p, space.namedIntersections[iv]!!)
        }

        When("material ← {shape_var}.material") { sv: String ->
            space.material = space.shapes[sv]!!.material
        }
        When("{shape_var}.material ← material") { sv: String ->
            space.shapes[sv]!!.material = space.material
        }

        When("intersections ← local_intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            space.intersections = space.shapes[sv]!!.localIntersect(space.rays[rv]!!)
        }
        When("{tuple_var} ← local_normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            space.tuples[tv] = space.shapes[sv]!!.localNormalAt(p, dummyIntersection())
        }
        When("{tuple_var} ← local_normal_at\\({shape_var}, {tuple_var})") { tv1: String, sv: String, tv2: String ->
            space.tuples[tv1] = space.shapes[sv]!!.localNormalAt(space.tuples[tv2]!!, dummyIntersection())
        }

        When("{tuple_var} ← world_to_object\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            space.tuples[tv] = space.shapes[sv]!!.worldToObject(p)
        }

        When("{tuple_var} ← normal_to_world\\({shape_var}, {vector})") { tv: String, sv: String, v: Tuple ->
            space.tuples[tv] = space.shapes[sv]!!.normalToWorld(v)
        }

        Then("{shape_var} = {shape_var}") { sv1: String, sv2: String ->
            assertThat(space.shapes[sv1]!!).isEqualTo(space.shapes[sv2]!!)
        }
        Then("{shape_var}.transform = {matrix_var}") { sv: String, mv: String ->
            assertThat(space.shapes[sv]!!.transform).isEqualTo(space.matrices[mv])
        }
        Then("{shape_var}.transform = {transform}") { sv: String, e: Matrix ->
            assertThat(space.shapes[sv]!!.transform).isEqualTo(e)
        }

        Then("{shape_var}.material = material") { sv: String ->
            assertThat(space.shapes[sv]!!.material).isEqualTo(space.material)
        }
        Then("{color_var} = {shape_var}.material.color") { cv: String, sv: String ->
            assertThat(space.colors[cv]!!).isEqualTo(space.shapes[sv]!!.material.color)
        }
        Then("{shape_var}.material.transparency = {real}") { sv: String, e: Double ->
            assertThat(space.shapes[sv]!!.material.transparency).isEqualTo(grey(e))
        }
        Then("{shape_var}.material.refractive_index = {real}") { sv: String, e: Double ->
            assertThat(space.shapes[sv]!!.material.refractiveIndex).isEqualTo(e)
        }

        Then("{shape_var}.saved_ray.origin = {point}") { sv: String, e: Tuple ->
            assertThat((space.shapes[sv] as TestShape).savedRay!!.origin).isEqualTo(e)
        }
        Then("{shape_var}.saved_ray.direction = {vector}") { sv: String, e: Tuple ->
            assertThat((space.shapes[sv] as TestShape).savedRay!!.direction).isEqualTo(e)
        }

        Then("{shape_var}.parent is nothing") { sv: String ->
            assertThat(space.shapes[sv]!!.parent).isNull()
        }
        Then("{shape_var}.parent = {shape_var}") { sv1: String, sv2: String ->
            assertThat(space.shapes[sv1]!!.parent).isEqualTo(space.shapes[sv2]!!)
        }
    }

    private fun dummyIntersection(): Intersection {
        return Intersection(0.0, Sphere(), 0.0, 0.0)
    }

    companion object {
        const val shapeVarRegex = "(?:outer|inner|lower|upper|shape|group)\\d*"

        fun configureFromDataTable(shape: Shape, dataTable: DataTable) {
            configureFromDataTableRows(shape, dataTable.asLists())
        }

        fun configureFromDataTableRows(shape: Shape, rows: List<List<String>>) {
            rows.forEach { row ->
                when (row[0]) {
                    "material.color" -> {
                        val params = row[1].substring(1, row[1].length - 1).split(", ")
                        shape.material = shape.material.build {
                            color = linearRgb(
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
                            ambient = grey(realFromString(row[1]))
                        }
                    }
                    "material.diffuse" -> {
                        shape.material = shape.material.build {
                            diffuse = grey(realFromString(row[1]))
                        }
                    }
                    "material.specular" -> {
                        shape.material = shape.material.build {
                            specular = grey(realFromString(row[1]))
                        }
                    }
                    "material.reflective" -> {
                        shape.material = shape.material.build {
                            reflective = grey(realFromString(row[1]))
                        }
                    }
                    "material.transparency" -> {
                        shape.material = shape.material.build {
                            transparency = grey(realFromString(row[1]))
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
}
