package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.*
import io.cucumber.java8.En
import org.trypticon.rocket.CommonParameterTypes.Companion.realFromString
import org.trypticon.rocket.CommonParameterTypes.Companion.realRegex
import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.RayStepDefinitions.Companion.rays
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapeVarRegex
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes

class IntersectionStepDefinitions: En {
    companion object {
        val intersections : MutableMap<String, Intersection> = mutableMapOf()
        lateinit var comps : Intersection.Precomputed
        lateinit var xs : List<Intersection>
        var reflectance : Double = 0.0
    }

    init {
        ParameterType("intersection_var", "i\\d*") { string -> string }

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
            intersections[iv] = Intersection(t, shapes[sv]!!)
        }

        When("xs ← intersect\\({shape_var}, {ray_var})") { sv: String, rv: String ->
            xs = shapes[sv]!!.intersect(rays[rv]!!)
        }
        When("xs ← intersections\\({intersection_var})") { iv: String ->
            xs = listOf(intersections[iv]!!)
        }
        When("xs ← intersections\\({intersection_var}, {intersection_var})") { iv1: String, iv2: String ->
            xs = listOf(intersections[iv1]!!, intersections[iv2]!!)
        }
        When("xs ← intersections\\({intersection_var}, {intersection_var}, {intersection_var}, {intersection_var})") {
                iv1: String, iv2: String, iv3: String, iv4: String ->
            xs = listOf(intersections[iv1]!!, intersections[iv2]!!, intersections[iv3]!!, intersections[iv4]!!)
        }
        When("xs ← {compact_intersections}") { cxs: List<Intersection> -> xs = cxs }

        When("{intersection_var} ← hit\\(xs)") { iv: String ->
            val i = Intersection.hit(xs)
            if (i != null) {
                intersections[iv] = i
            } else {
                intersections.remove(iv)
            }
        }

        When("comps ← prepare_computations\\({intersection_var}, {ray_var})") { iv: String, rv: String ->
            comps = intersections[iv]!!.prepareComputations(rays[rv]!!, listOf(intersections[iv]!!))
        }
        When("comps ← prepare_computations\\({intersection_var}, {ray_var}, xs)") { iv: String, rv: String ->
            comps = intersections[iv]!!.prepareComputations(rays[rv]!!, xs)
        }
        When("comps ← prepare_computations\\(xs\\[{int}], {ray_var}, xs)") { i: Int, rv: String ->
            comps = xs[i].prepareComputations(rays[rv]!!, xs)
        }

        When("^reflectance ← schlick\\(comps\\)") {
            reflectance = comps.schlick()
        }

        Then("{intersection_var} = {intersection_var}") { iv1: String, iv2: String ->
            assertThat(intersections[iv1]!!).isEqualTo(intersections[iv2]!!)
        }
        Then("{intersection_var} is nothing") { iv: String ->
            assertThat(intersections[iv]).isNull()
        }

        Then("{intersection_var}.t = {real}") { iv: String, t: Double ->
            assertThat(intersections[iv]!!.t).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.object = {shape_var}") { iv: String, sv: String ->
            assertThat(intersections[iv]!!.obj).isEqualTo(shapes[sv]!!)
        }

        Then("xs is empty") {
            assertThat(xs).isEmpty()
        }
        Then("xs.count = {int}") { e: Int ->
            assertThat(xs.size).isEqualTo(e)
        }
        Then("xs[{int}].t = {real}") { i: Int, e: Double ->
            assertThat(xs[i].t).isCloseTo(e, epsilon)
        }
        Then("xs[{int}].object = {shape_var}") { i: Int, sv: String ->
            assertThat(xs[i].obj).isEqualTo(shapes[sv]!!)
        }

        Then("comps.t = {intersection_var}.t") { iv: String ->
            assertThat(comps.t).isEqualTo(intersections[iv]!!.t)
        }
        Then("comps.object = {intersection_var}.object") { iv: String ->
            assertThat(comps.obj).isEqualTo(intersections[iv]!!.obj)
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
        Then("comps.eyev = {vector}") { e: Tuple ->
            assertThat(comps.eyeVector).isCloseTo(e, epsilon)
        }
        Then("comps.normalv = {vector}") { e: Tuple ->
            assertThat(comps.normal).isCloseTo(e, epsilon)
        }
        Then("comps.reflectv = {vector}") { e: Tuple ->
            assertThat(comps.reflectVector).isCloseTo(e, epsilon)
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