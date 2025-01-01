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
class IntersectionStepDefinitions(space: Space) : En {
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
                Intersection(realFromString(parts[0]), space.shapes[parts[1]]!!)
            }
        }

        Given("{intersection_var} ← intersection\\({real}, {shape_var})") { iv: String, t: Double, sv: String ->
            space.namedIntersections[iv] = Intersection(t, space.shapes[sv]!!)
        }
        When(
            "{intersection_var} ← intersection_with_uv\\({real}, {shape_var}, {real}, {real})"
        ) { iv: String, t: Double, sv: String, u: Double, v: Double ->
            space.namedIntersections[iv] = Intersection(t, space.shapes[sv]!!, u, v)
        }

        When("intersections ← intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            space.intersections = Intersections(space.shapes[sv]!!.intersect(space.rays[rv]!!))
        }
        When("intersections ← intersections\\({intersection_var})") { iv: String ->
            space.intersections = Intersections(space.namedIntersections[iv]!!)
        }
        When("intersections ← intersections\\({intersection_var}, {intersection_var})") { iv1: String, iv2: String ->
            space.intersections = sequenceOf(space.namedIntersections[iv1]!!, space.namedIntersections[iv2]!!)
                .toIntersections()
        }
        When("intersections ← intersections\\({intersection_var}, {intersection_var}, {intersection_var}, {intersection_var})") { iv1: String, iv2: String, iv3: String, iv4: String ->
            space.intersections = sequenceOf(
                space.namedIntersections[iv1]!!,
                space.namedIntersections[iv2]!!,
                space.namedIntersections[iv3]!!,
                space.namedIntersections[iv4]!!
            )
                .toIntersections()
        }
        When("intersections ← {compact_intersections}") { cxs: List<Intersection> ->
            space.intersections = Intersections(cxs)
        }

        When("{intersection_var} ← hit\\(intersections)") { iv: String ->
            val i = space.intersections.hit()
            if (i != null) {
                space.namedIntersections[iv] = i
            } else {
                space.namedIntersections.remove(iv)
            }
        }

        When("comps ← prepare_computations\\({intersection_var}, {ray_var})") { iv: String, rv: String ->
            space.comps = space.namedIntersections[iv]!!
                .prepareComputations(space.rays[rv]!!, Intersections(space.namedIntersections[iv]!!))
        }
        When("comps ← prepare_computations\\({intersection_var}, {ray_var}, intersections)") { iv: String, rv: String ->
            space.comps = space.namedIntersections[iv]!!
                .prepareComputations(space.rays[rv]!!, space.intersections)
        }
        When("comps ← prepare_computations\\(intersections[{int}], {ray_var}, intersections)") { i: Int, rv: String ->
            space.comps = space.intersections[i].prepareComputations(space.rays[rv]!!, space.intersections)
        }

        When("^reflectance ← fresnel\\(comps\\)") {
            reflectance = space.comps.fresnel()
        }

        Then("{intersection_var} = {intersection_var}") { iv1: String, iv2: String ->
            assertThat(space.namedIntersections[iv1]!!).isEqualTo(space.namedIntersections[iv2]!!)
        }
        Then("{intersection_var} is nothing") { iv: String ->
            assertThat(space.namedIntersections[iv]).isNull()
        }

        Then("{intersection_var}.t = {real}") { iv: String, t: Double ->
            assertThat(space.namedIntersections[iv]!!.t).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.object = {shape_var}") { iv: String, sv: String ->
            assertThat(space.namedIntersections[iv]!!.obj).isEqualTo(space.shapes[sv]!!)
        }
        Then("{intersection_var}.u = {real}") { iv: String, t: Double ->
            assertThat(space.namedIntersections[iv]!!.u).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.v = {real}") { iv: String, t: Double ->
            assertThat(space.namedIntersections[iv]!!.v).isCloseTo(t, epsilon)
        }

        Then("intersections is empty") {
            assertThat(space.intersections).isEmpty()
        }
        Then("intersections.count = {int}") { e: Int ->
            assertThat(space.intersections.size).isEqualTo(e)
        }
        Then("intersections[{int}].t = {real}") { i: Int, e: Double ->
            assertThat(space.intersections[i].t).isCloseTo(e, epsilon)
        }
        Then("intersections[{int}].object = {shape_var}") { i: Int, sv: String ->
            assertThat(space.intersections[i].obj).isEqualTo(space.shapes[sv]!!)
        }
        Then("intersections[{int}].u = {real}") { i: Int, e: Double ->
            assertThat(space.intersections[i].u).isCloseTo(e, epsilon)
        }
        Then("intersections[{int}].v = {real}") { i: Int, e: Double ->
            assertThat(space.intersections[i].v).isCloseTo(e, epsilon)
        }

        Then("comps.t = {intersection_var}.t") { iv: String ->
            assertThat(space.comps.t).isEqualTo(space.namedIntersections[iv]!!.t)
        }
        Then("comps.object = {intersection_var}.object") { iv: String ->
            assertThat(space.comps.obj).isEqualTo(space.namedIntersections[iv]!!.obj)
        }
        Then("comps.point = {point}") { e: Tuple ->
            assertThat(space.comps.point).isCloseTo(e, epsilon)
        }
        Then("^comps.over_point.z < -EPSILON/2") {
            assertThat(space.comps.overPoint.z).isLessThan(-epsilon / 2.0)
        }
        Then("comps.point.z > comps.over_point.z") {
            assertThat(space.comps.point.z).isGreaterThan(space.comps.overPoint.z)
        }
        Then("^comps.under_point.z > EPSILON/2") {
            assertThat(space.comps.underPoint.z).isGreaterThan(-epsilon / 2.0)
        }
        Then("^comps.point.z < comps.under_point.z") {
            assertThat(space.comps.point.z).isLessThan(space.comps.underPoint.z)
        }
        Then("comps.eyeline = {vector}") { e: Tuple ->
            assertThat(space.comps.eyeline).isCloseTo(e, epsilon)
        }
        Then("comps.normal = {vector}") { e: Tuple ->
            assertThat(space.comps.normal).isCloseTo(e, epsilon)
        }
        Then("comps.tangent = {vector}") { e: Tuple ->
            assertThat(space.comps.tangent).isCloseTo(e, epsilon)
        }
        Then("comps.bitangent = {vector}") { e: Tuple ->
            assertThat(space.comps.bitangent).isCloseTo(e, epsilon)
        }
        Then("comps.reflection = {vector}") { e: Tuple ->
            assertThat(space.comps.reflection).isCloseTo(e, epsilon)
        }
        Then("comps.inside = {boolean}") { e: Boolean ->
            assertThat(space.comps.isInside).isEqualTo(e)
        }
        Then("comps.n1 = {real}") { e: Double ->
            assertThat(space.comps.n1).isEqualTo(e)
        }
        Then("comps.n2 = {real}") { e: Double ->
            assertThat(space.comps.n2).isEqualTo(e)
        }

        Then("reflectance = {real}") { e: Double ->
            assertThat(reflectance).isCloseTo(e, epsilon)
        }
    }
}
