package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.*
import io.cucumber.java8.En
import org.trypticon.rocket.CommonParameterTypes.Companion.epsilon
import org.trypticon.rocket.RayStepDefinitions.Companion.rays
import org.trypticon.rocket.SphereStepDefinitions.Companion.spheres

class IntersectionStepDefinitions: En {
    companion object {
        val intersections : MutableMap<String, Intersection> = mutableMapOf()
        lateinit var comps : Intersection.Precomputed
        lateinit var xs : List<Intersection>
    }

    init {
        ParameterType("intersection_var", "i\\d*") { string -> string }

        Given("{intersection_var} ← intersection\\({real}, {sphere_var})") { iv: String, t: Double, sv: String ->
            intersections[iv] = Intersection(t, spheres[sv]!!)
        }

        When("xs ← intersect\\({sphere_var}, {ray_var})") { sv: String, rv: String ->
            xs = spheres[sv]!!.intersect(rays[rv]!!)
        }
        When("xs ← intersections\\({intersection_var}, {intersection_var})") { iv1: String, iv2: String ->
            xs = listOf(intersections[iv1]!!, intersections[iv2]!!)
        }
        When("xs ← intersections\\({intersection_var}, {intersection_var}, {intersection_var}, {intersection_var})") {
                iv1: String, iv2: String, iv3: String, iv4: String ->
            xs = listOf(intersections[iv1]!!, intersections[iv2]!!, intersections[iv3]!!, intersections[iv4]!!)
        }

        When("{intersection_var} ← hit\\(xs)") { iv: String ->
            val i = Intersection.hit(xs)
            if (i != null) {
                intersections[iv] = i
            } else {
                intersections.remove(iv)
            }
        }

        When("comps ← prepare_computations\\({intersection_var}, {ray_var})") { iv: String, rv: String ->
            comps = intersections[iv]!!.prepareComputations(rays[rv]!!)
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
        Then("{intersection_var}.object = {sphere_var}") { iv: String, sv: String ->
            assertThat(intersections[iv]!!.obj).isEqualTo(spheres[sv]!!)
        }

        Then("xs.count = {int}") { e: Int ->
            assertThat(xs.size).isEqualTo(e)
        }
        Then("xs[{int}].t = {real}") { i: Int, e: Double ->
            assertThat(xs[i].t).isCloseTo(e, epsilon)
        }
        Then("xs[{int}].object = {sphere_var}") { i: Int, sv: String ->
            assertThat(xs[i].obj).isEqualTo(spheres[sv]!!)
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
        Then("comps.eyev = {vector}") { e: Tuple ->
            assertThat(comps.eyeVector).isCloseTo(e, epsilon)
        }
        Then("comps.normalv = {vector}") { e: Tuple ->
            assertThat(comps.normal).isCloseTo(e, epsilon)
        }
        Then("comps.inside = {boolean}") { e: Boolean ->
            assertThat(comps.inside).isEqualTo(e)
        }
    }
}