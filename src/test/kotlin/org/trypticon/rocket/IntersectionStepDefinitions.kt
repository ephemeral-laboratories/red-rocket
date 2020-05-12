import RayStepDefinitions.Companion.rays
import SphereStepDefinitions.Companion.s
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.cucumber.java8.En

class IntersectionStepDefinitions: En {
    private val epsilon: Double = 0.00001

    companion object {
        val intersections : MutableMap<String, Intersection> = mutableMapOf()
        lateinit var xs : List<Intersection>
    }

    init {
        ParameterType("intersection_var", "i\\d*") { string -> string }

        Given("{intersection_var} ← intersection\\({real}, s)") { v: String, t: Double ->
            intersections[v] = Intersection(t, s)
        }

        When("xs ← intersect\\(s, {ray_var})") { v: String ->
            xs = s.intersect(rays[v]!!)
        }
        When("xs ← intersections\\({intersection_var}, {intersection_var})") { v1: String, v2: String ->
            xs = listOf(intersections[v1]!!, intersections[v2]!!)
        }
        When("xs ← intersections\\({intersection_var}, {intersection_var}, {intersection_var}, {intersection_var})") {
                v1: String, v2: String, v3: String, v4: String ->
            xs = listOf(intersections[v1]!!, intersections[v2]!!, intersections[v3]!!, intersections[v4]!!)
        }

        When("{intersection_var} ← hit\\(xs)") { v: String ->
            val i = Intersection.hit(xs)
            if (i != null) {
                intersections[v] = i
            } else {
                intersections.remove(v)
            }
        }

        Then("{intersection_var} = {intersection_var}") { v1: String, v2: String ->
            assertThat(intersections[v1]!!).isEqualTo(intersections[v2]!!)
        }
        Then("{intersection_var} is nothing") { v: String ->
            assertThat(intersections[v]).isNull()
        }

        Then("{intersection_var}.t = {real}") { v: String, t: Double ->
            assertThat(intersections[v]!!.t).isCloseTo(t, epsilon)
        }
        Then("{intersection_var}.object = s") { v: String ->
            assertThat(intersections[v]!!.obj).isEqualTo(s)
        }

        Then("xs.count = {int}") { e: Int ->
            assertThat(xs.size).isEqualTo(e)
        }
        Then("xs[{int}].t = {real}") { i: Int, e: Double ->
            assertThat(xs[i].t).isCloseTo(e, epsilon)
        }
        Then("xs[{int}].object = s") { i: Int ->
            assertThat(xs[i].obj).isEqualTo(s)
        }
    }
}