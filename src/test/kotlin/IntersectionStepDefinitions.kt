package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import assertk.assertions.isNull
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class IntersectionStepDefinitions(universe: Universe) : En {
    private var reflectance: Double = 0.0

    init {
        ParameterType("intersection_var", "intersection\\d*") { string -> string }

        ParameterType(
            "compact_intersections",
            "intersections\\(" +
                "((?:(?:$realRegex):(?:$shapeVarRegex)(?:,\\s*)?)+)" +
                "\\)"
        ) { string ->
            string.split(", ").map { param ->
                val parts = param.split(':')
                Intersection(realFromString(parts[0]), universe.shapes[parts[1]]!!)
            }
        }

        Given("{intersection_var} ← intersection\\({real}, {shape_var})") { iv: String, t: Double, sv: String ->
            universe.namedIntersections[iv] = Intersection(t, universe.shapes[sv]!!)
        }
        When(
            "{intersection_var} ← intersection_with_uv\\({real}, {shape_var}, {real}, {real})"
        ) { iv: String, t: Double, sv: String, u: Double, v: Double ->
            universe.namedIntersections[iv] = Intersection(t, universe.shapes[sv]!!, u, v)
        }

        When("intersections ← intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            universe.intersections = Intersections(universe.shapes[sv]!!.intersect(universe.rays[rv]!!))
        }
        When("intersections ← intersections\\({intersection_var})") { iv: String ->
            universe.intersections = Intersections(universe.namedIntersections[iv]!!)
        }
        When("intersections ← intersections\\({intersection_var}, {intersection_var})") { iv1: String, iv2: String ->
            universe.intersections = sequenceOf(universe.namedIntersections[iv1]!!, universe.namedIntersections[iv2]!!)
                .toIntersections()
        }
        When("intersections ← intersections\\({intersection_var}, {intersection_var}, {intersection_var}, {intersection_var})") { iv1: String, iv2: String, iv3: String, iv4: String ->
            universe.intersections = sequenceOf(
                universe.namedIntersections[iv1]!!,
                universe.namedIntersections[iv2]!!,
                universe.namedIntersections[iv3]!!,
                universe.namedIntersections[iv4]!!
            )
                .toIntersections()
        }
        When("intersections ← {compact_intersections}") { cxs: List<Intersection> ->
            universe.intersections = Intersections(cxs)
        }

        When("{intersection_var} ← hit\\(intersections)") { iv: String ->
            val i = universe.intersections.hit()
            if (i != null) {
                universe.namedIntersections[iv] = i
            } else {
                universe.namedIntersections.remove(iv)
            }
        }

        When("comps ← prepare_computations\\({intersection_var}, {ray_var})") { iv: String, rv: String ->
            universe.comps = universe.namedIntersections[iv]!!
                .prepareComputations(universe.rays[rv]!!, Intersections(universe.namedIntersections[iv]!!))
        }
        When("comps ← prepare_computations\\({intersection_var}, {ray_var}, intersections)") { iv: String, rv: String ->
            universe.comps = universe.namedIntersections[iv]!!
                .prepareComputations(universe.rays[rv]!!, universe.intersections)
        }
        When("comps ← prepare_computations\\(intersections[{int}], {ray_var}, intersections)") { i: Int, rv: String ->
            universe.comps = universe.intersections[i].prepareComputations(universe.rays[rv]!!, universe.intersections)
        }

        When("^reflectance ← fresnel\\(comps\\)") {
            reflectance = universe.comps.fresnel()
        }

        Then("{intersection_var} = {intersection_var}") { iv1: String, iv2: String ->
            assertThat(universe.namedIntersections[iv1]!!).isEqualTo(universe.namedIntersections[iv2]!!)
        }
        Then("{intersection_var} is nothing") { iv: String ->
            assertThat(universe.namedIntersections[iv]).isNull()
        }

        Then("{intersection_var}.t = {real}") { iv: String, t: Double ->
            assertThat(universe.namedIntersections[iv]!!.t).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.object = {shape_var}") { iv: String, sv: String ->
            assertThat(universe.namedIntersections[iv]!!.obj).isEqualTo(universe.shapes[sv]!!)
        }
        Then("{intersection_var}.u = {real}") { iv: String, t: Double ->
            assertThat(universe.namedIntersections[iv]!!.u).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.v = {real}") { iv: String, t: Double ->
            assertThat(universe.namedIntersections[iv]!!.v).isCloseTo(t, epsilon)
        }

        Then("intersections is empty") {
            assertThat(universe.intersections).isEmpty()
        }
        Then("intersections.count = {int}") { e: Int ->
            assertThat(universe.intersections.size).isEqualTo(e)
        }
        Then("intersections[{int}].t = {real}") { i: Int, e: Double ->
            assertThat(universe.intersections[i].t).isCloseTo(e, epsilon)
        }
        Then("intersections[{int}].object = {shape_var}") { i: Int, sv: String ->
            assertThat(universe.intersections[i].obj).isEqualTo(universe.shapes[sv]!!)
        }
        Then("intersections[{int}].u = {real}") { i: Int, e: Double ->
            assertThat(universe.intersections[i].u).isCloseTo(e, epsilon)
        }
        Then("intersections[{int}].v = {real}") { i: Int, e: Double ->
            assertThat(universe.intersections[i].v).isCloseTo(e, epsilon)
        }

        Then("comps.t = {intersection_var}.t") { iv: String ->
            assertThat(universe.comps.t).isEqualTo(universe.namedIntersections[iv]!!.t)
        }
        Then("comps.object = {intersection_var}.object") { iv: String ->
            assertThat(universe.comps.obj).isEqualTo(universe.namedIntersections[iv]!!.obj)
        }
        Then("comps.point = {point}") { e: Tuple ->
            assertThat(universe.comps.point).isCloseTo(e, epsilon)
        }
        Then("^comps.over_point.z < -EPSILON/2") {
            assertThat(universe.comps.overPoint.z).isLessThan(-epsilon / 2.0)
        }
        Then("comps.point.z > comps.over_point.z") {
            assertThat(universe.comps.point.z).isGreaterThan(universe.comps.overPoint.z)
        }
        Then("^comps.under_point.z > EPSILON/2") {
            assertThat(universe.comps.underPoint.z).isGreaterThan(-epsilon / 2.0)
        }
        Then("^comps.point.z < comps.under_point.z") {
            assertThat(universe.comps.point.z).isLessThan(universe.comps.underPoint.z)
        }
        Then("comps.eyeline = {vector}") { e: Tuple ->
            assertThat(universe.comps.eyeline).isCloseTo(e, epsilon)
        }
        Then("comps.normal = {vector}") { e: Tuple ->
            assertThat(universe.comps.normal).isCloseTo(e, epsilon)
        }
        Then("comps.tangent = {vector}") { e: Tuple ->
            assertThat(universe.comps.tangent).isCloseTo(e, epsilon)
        }
        Then("comps.bitangent = {vector}") { e: Tuple ->
            assertThat(universe.comps.bitangent).isCloseTo(e, epsilon)
        }
        Then("comps.reflection = {vector}") { e: Tuple ->
            assertThat(universe.comps.reflection).isCloseTo(e, epsilon)
        }
        Then("comps.inside = {boolean}") { e: Boolean ->
            assertThat(universe.comps.isInside).isEqualTo(e)
        }
        Then("comps.n1 = {real}") { e: Double ->
            assertThat(universe.comps.n1).isEqualTo(e)
        }
        Then("comps.n2 = {real}") { e: Double ->
            assertThat(universe.comps.n2).isEqualTo(e)
        }

        Then("reflectance = {real}") { e: Double ->
            assertThat(reflectance).isCloseTo(e, epsilon)
        }
    }
}
