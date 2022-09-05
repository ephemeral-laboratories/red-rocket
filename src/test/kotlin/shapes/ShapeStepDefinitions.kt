package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.MatrixStepDefinitions.Companion.transformFromString
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Universe
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import garden.ephemeral.rocket.patterns.TestPattern
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class ShapeStepDefinitions(universe: Universe) : En {
    init {
        ParameterType("shape_var", shapeVarRegex) { string -> string }

        Given("^($shapeVarRegex) ← test_shape\\(\\)") { sv: String -> universe.shapes[sv] = TestShape() }

        Given("{shape_var} has:") { sv: String, dataTable: DataTable ->
            configureFromDataTable(universe.shapes[sv]!!, dataTable)
        }

        Given("{shape_var}.material.ambient ← {real}") { sv: String, v: Double ->
            universe.shapes[sv]!!.material = universe.shapes[sv]!!.material.build {
                ambient = grey(v)
            }
        }

        When("set_transform\\({shape_var}, {matrix_var})") { sv: String, mv: String ->
            universe.shapes[sv]!!.transform = universe.matrices[mv]!!
        }
        When("set_transform\\({shape_var}, {transform})") { sv: String, transform: Matrix ->
            universe.shapes[sv]!!.transform = transform
        }

        When("{tuple_var} ← normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            universe.tuples[tv] = universe.shapes[sv]!!.worldNormalAt(p, dummyIntersection())
        }
        When("{tuple_var} ← normal_at\\({shape_var}, {point}, {intersection_var})") {
                tv: String, sv: String, p: Tuple, iv: String ->
            universe.tuples[tv] = universe.shapes[sv]!!.worldNormalAt(p, universe.namedIntersections[iv]!!)
        }

        When("material ← {shape_var}.material") { sv: String ->
            universe.material = universe.shapes[sv]!!.material
        }
        When("{shape_var}.material ← material") { sv: String ->
            universe.shapes[sv]!!.material = universe.material
        }

        When("intersections ← local_intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            universe.intersections = Intersections(universe.shapes[sv]!!.localIntersect(universe.rays[rv]!!))
        }
        When("{tuple_var} ← local_normal_at\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            universe.tuples[tv] = universe.shapes[sv]!!.localNormalAt(p, dummyIntersection())
        }
        When("{tuple_var} ← local_normal_at\\({shape_var}, {tuple_var})") { tv1: String, sv: String, tv2: String ->
            universe.tuples[tv1] = universe.shapes[sv]!!.localNormalAt(universe.tuples[tv2]!!, dummyIntersection())
        }

        When("{tuple_var} ← world_to_object\\({shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            universe.tuples[tv] = universe.shapes[sv]!!.worldToObject(p)
        }

        When("{tuple_var} ← normal_to_world\\({shape_var}, {vector})") { tv: String, sv: String, v: Tuple ->
            universe.tuples[tv] = universe.shapes[sv]!!.normalToWorld(v)
        }

        Then("{shape_var} = {shape_var}") { sv1: String, sv2: String ->
            assertThat(universe.shapes[sv1]!!).isEqualTo(universe.shapes[sv2]!!)
        }
        Then("{shape_var}.transform = {matrix_var}") { sv: String, mv: String ->
            assertThat(universe.shapes[sv]!!.transform).isEqualTo(universe.matrices[mv])
        }
        Then("{shape_var}.transform = {transform}") { sv: String, e: Matrix ->
            assertThat(universe.shapes[sv]!!.transform).isEqualTo(e)
        }

        Then("{shape_var}.material = material") { sv: String ->
            assertThat(universe.shapes[sv]!!.material).isEqualTo(universe.material)
        }
        Then("{color_var} = {shape_var}.material.color") { cv: String, sv: String ->
            assertThat(universe.colors[cv]!!).isEqualTo(universe.shapes[sv]!!.material.color)
        }
        Then("{shape_var}.material.transparency = {real}") { sv: String, e: Double ->
            assertThat(universe.shapes[sv]!!.material.transparency).isEqualTo(grey(e))
        }
        Then("{shape_var}.material.refractive_index = {real}") { sv: String, e: Double ->
            assertThat(universe.shapes[sv]!!.material.refractiveIndex).isEqualTo(e)
        }

        Then("{shape_var}.saved_ray.origin = {point}") { sv: String, e: Tuple ->
            assertThat((universe.shapes[sv] as TestShape).savedRay!!.origin).isEqualTo(e)
        }
        Then("{shape_var}.saved_ray.direction = {vector}") { sv: String, e: Tuple ->
            assertThat((universe.shapes[sv] as TestShape).savedRay!!.direction).isEqualTo(e)
        }

        Then("{shape_var}.parent is nothing") { sv: String ->
            assertThat(universe.shapes[sv]!!.parent).isNull()
        }
        Then("{shape_var}.parent = {shape_var}") { sv1: String, sv2: String ->
            assertThat(universe.shapes[sv1]!!.parent).isEqualTo(universe.shapes[sv2]!!)
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
