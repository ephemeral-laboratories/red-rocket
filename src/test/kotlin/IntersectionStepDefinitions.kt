package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.*
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.RayStepDefinitions.Companion.rays
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En

class IntersectionStepDefinitions: En {
    companion object {
        val namedIntersections : MutableMap<String, Intersection> = mutableMapOf()
        lateinit var comps : Intersection.Precomputed
        lateinit var intersections : List<Intersection>
        var reflectance : Double = 0.0
    }

    init {
        ParameterType("intersection_var", "intersection\\d*") { string -> string }

        ParameterType("compact_intersections",
            "intersections\\(" +
                    "((?:(?:$realRegex):(?:$shapeVarRegex)(?:,\\s*)?)+)" +
                    "\\)") { string ->

            string.split(", ").map { param ->
                val parts = param.split(':')
                Intersection(realFromString(parts[0]), shapes[parts[1]]!!)
            }
        }

        Given("{intersection_var} ← intersection\\({real}, {shape_var})") { iv: String, t: Double, sv: String ->
            namedIntersections[iv] = Intersection(t, shapes[sv]!!)
        }
        When("{intersection_var} ← intersection_with_uv\\({real}, {shape_var}, {real}, {real})") {
                iv: String, t: Double, sv: String, u: Double, v: Double ->
            namedIntersections[iv] = Intersection(t, shapes[sv]!!, u, v)
        }

        When("intersections ← intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            intersections = shapes[sv]!!.intersect(rays[rv]!!)
        }
        When("intersections ← intersections\\({intersection_var})") { iv: String ->
            intersections = listOf(namedIntersections[iv]!!)
        }
        When("intersections ← intersections\\({intersection_var}, {intersection_var})") { iv1: String, iv2: String ->
            intersections = listOf(namedIntersections[iv1]!!, namedIntersections[iv2]!!)
        }
        When("intersections ← intersections\\({intersection_var}, {intersection_var}, {intersection_var}, {intersection_var})") {
                iv1: String, iv2: String, iv3: String, iv4: String ->
            intersections = listOf(namedIntersections[iv1]!!, namedIntersections[iv2]!!, namedIntersections[iv3]!!, namedIntersections[iv4]!!)
        }
        When("intersections ← {compact_intersections}") { cxs: List<Intersection> -> intersections = cxs }

        When("{intersection_var} ← hit\\(intersections)") { iv: String ->
            val i = Intersection.hit(intersections)
            if (i != null) {
                namedIntersections[iv] = i
            } else {
                namedIntersections.remove(iv)
            }
        }

        When("comps ← prepare_computations\\({intersection_var}, {ray_var})") { iv: String, rv: String ->
            comps = namedIntersections[iv]!!.prepareComputations(rays[rv]!!, listOf(namedIntersections[iv]!!))
        }
        When("comps ← prepare_computations\\({intersection_var}, {ray_var}, intersections)") { iv: String, rv: String ->
            comps = namedIntersections[iv]!!.prepareComputations(rays[rv]!!, intersections)
        }
        When("comps ← prepare_computations\\(intersections\\[{int}], {ray_var}, intersections)") { i: Int, rv: String ->
            comps = intersections[i].prepareComputations(rays[rv]!!, intersections)
        }

        When("^reflectance ← schlick\\(comps\\)") {
            reflectance = comps.schlick()
        }

        Then("{intersection_var} = {intersection_var}") { iv1: String, iv2: String ->
            assertThat(namedIntersections[iv1]!!).isEqualTo(namedIntersections[iv2]!!)
        }
        Then("{intersection_var} is nothing") { iv: String ->
            assertThat(namedIntersections[iv]).isNull()
        }

        Then("{intersection_var}.t = {real}") { iv: String, t: Double ->
            assertThat(namedIntersections[iv]!!.t).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.object = {shape_var}") { iv: String, sv: String ->
            assertThat(namedIntersections[iv]!!.obj).isEqualTo(shapes[sv]!!)
        }
        Then("{intersection_var}.u = {real}") { iv: String, t: Double ->
            assertThat(namedIntersections[iv]!!.u).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.v = {real}") { iv: String, t: Double ->
            assertThat(namedIntersections[iv]!!.v).isCloseTo(t, epsilon)
        }

        Then("intersections is empty") {
            assertThat(intersections).isEmpty()
        }
        Then("intersections.count = {int}") { e: Int ->
            assertThat(intersections.size).isEqualTo(e)
        }
        Then("intersections[{int}].t = {real}") { i: Int, e: Double ->
            assertThat(intersections[i].t).isCloseTo(e, epsilon)
        }
        Then("intersections[{int}].object = {shape_var}") { i: Int, sv: String ->
            assertThat(intersections[i].obj).isEqualTo(shapes[sv]!!)
        }
        Then("intersections[{int}].u = {real}") { i: Int, e: Double ->
            assertThat(intersections[i].u).isCloseTo(e, epsilon)
        }
        Then("intersections[{int}].v = {real}") { i: Int, e: Double ->
            assertThat(intersections[i].v).isCloseTo(e, epsilon)
        }

        Then("comps.t = {intersection_var}.t") { iv: String ->
            assertThat(comps.t).isEqualTo(namedIntersections[iv]!!.t)
        }
        Then("comps.object = {intersection_var}.object") { iv: String ->
            assertThat(comps.obj).isEqualTo(namedIntersections[iv]!!.obj)
        }
        Then("comps.point = {point}") { e: Tuple ->
            assertThat(comps.point).isCloseTo(e, epsilon)
        }
        Then("^comps.over_point.z < -EPSILON/2") {
            assertThat(comps.overPoint.z).isLessThan(-epsilon / 2.0)
        }
        Then("comps.point.z > comps.over_point.z") {
            assertThat(comps.point.z).isGreaterThan(comps.overPoint.z)
        }
        Then("^comps.under_point.z > EPSILON/2") {
            assertThat(comps.underPoint.z).isGreaterThan(-epsilon / 2.0)
        }
        Then("^comps.point.z < comps.under_point.z") {
            assertThat(comps.point.z).isLessThan(comps.underPoint.z)
        }
        Then("comps.eyeline = {vector}") { e: Tuple ->
            assertThat(comps.eyeline).isCloseTo(e, epsilon)
        }
        Then("comps.normal = {vector}") { e: Tuple ->
            assertThat(comps.normal).isCloseTo(e, epsilon)
        }
        Then("comps.tangent = {vector}") { e: Tuple ->
            assertThat(comps.tangent).isCloseTo(e, epsilon)
        }
        Then("comps.bitangent = {vector}") { e: Tuple ->
            assertThat(comps.bitangent).isCloseTo(e, epsilon)
        }
        Then("comps.reflection = {vector}") { e: Tuple ->
            assertThat(comps.reflection).isCloseTo(e, epsilon)
        }
        Then("comps.inside = {boolean}") { e: Boolean ->
            assertThat(comps.inside).isEqualTo(e)
        }
        Then("comps.n1 = {real}") { e: Double ->
            assertThat(comps.n1).isEqualTo(e)
        }
        Then("comps.n2 = {real}") { e: Double ->
            assertThat(comps.n2).isEqualTo(e)
        }

        Then("reflectance = {real}") { e: Double ->
            assertThat(reflectance).isCloseTo(e, epsilon)
        }
    }
}