package garden.ephemeral.rocket.shapes

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Space
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class CSGStepDefinitions(space: Space) : En {
    private var result: Boolean = false
    private lateinit var resultIntersections: List<Intersection>

    init {
        Given("{shape_var} ← csg\\({string}, {sphere}, {cube})") { sv: String, string: String, s1: Shape, s2: Shape ->
            space.shapes[sv] = CSG(operationFromString(string), s1, s2)
        }
        When("{shape_var} ← csg\\({string}, {shape_var}, {shape_var})") {
                sv1: String, string: String, sv2: String, sv3: String ->
            space.shapes[sv1] = CSG(operationFromString(string), space.shapes[sv2]!!, space.shapes[sv3]!!)
        }

        When("result ← intersection_allowed\\({string}, {boolean}, {boolean}, {boolean})") {
                string: String, lHit: Boolean, inL: Boolean, inR: Boolean ->
            result = operationFromString(string).intersectionAllowed(lHit, inL, inR)
        }

        When("result ← filter_intersections\\({shape_var}, intersections)") { sv: String ->
            resultIntersections = (space.shapes[sv] as CSG).filterIntersections(space.intersections)
        }

        Then("{shape_var}.operation = {string}") { sv: String, string: String ->
            assertThat((space.shapes[sv] as CSG).operation).isEqualTo(operationFromString(string))
        }
        Then("{shape_var}.left = {shape_var}") { sv1: String, sv2: String ->
            assertThat((space.shapes[sv1] as CSG).left).isEqualTo(space.shapes[sv2]!!)
        }
        Then("{shape_var}.right = {shape_var}") { sv1: String, sv2: String ->
            assertThat((space.shapes[sv1] as CSG).right).isEqualTo(space.shapes[sv2]!!)
        }

        Then("result = {boolean}") { e: Boolean -> assertThat(result).isEqualTo(e) }

        Then("result.count = {int}") { e: Int -> assertThat(resultIntersections.size).isEqualTo(e) }
        Then("result[{int}] = intersections[{int}]") { i1: Int, i2: Int ->
            assertThat(resultIntersections[i1]).isEqualTo(space.intersections[i2])
        }
    }

    private fun operationFromString(string: String): CSG.Operation {
        return when (string) {
            "union" -> CSG.Operation.UNION
            "intersection" -> CSG.Operation.INTERSECTION
            "difference" -> CSG.Operation.DIFFERENCE
            else -> throw IllegalArgumentException("Unknown operation: $string")
        }
    }
}
