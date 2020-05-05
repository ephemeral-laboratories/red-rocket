
import Tuple.Companion.color
import Tuple.Companion.point
import Tuple.Companion.vector
import assertk.Assert
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.matchesPredicate
import io.cucumber.java8.En


class TupleStepDefinitions: En {
    private val epsilon: Double = 0.00001

    companion object {
        val tuples: MutableMap<String, Tuple> = mutableMapOf()
    }

    init {
        ParameterType("var", "[a-z][a-z0-9]*") { string -> string }

        // I can smell this getting much hairier than this, but it would be nice if I could do sweet stuff
        // like (1 + √5)/2 eventually so let's get this started.
        ParameterType("real", "-?√?\\d+\\.\\d+") { string: String ->
            var s = string
            var negative = false
            var root = false
            if (s.startsWith("-")) {
                s = s.substring(1)
                negative = true
            }
            if (s.startsWith("√")) {
                s = s.substring(1)
                root = true
            }
            var n = s.toDouble()
            if (root) {
                n = Math.sqrt(n)
            }
            if (negative) {
                n = -n
            }
            n
        }

        Given("{var} ← tuple\\({real}, {real}, {real}, {real})") { v: String, x: Double, y: Double, z: Double, w: Double ->
            tuples[v] = Tuple(x, y, z, w)
        }

        Given("{var} ← point\\({real}, {real}, {real})") { v: String, x: Double, y: Double, z: Double -> tuples[v] = point(x, y, z) }
        Given("{var} ← vector\\({real}, {real}, {real})") { v: String, x: Double, y: Double, z: Double -> tuples[v] = vector(x, y, z) }
        Given("{var} ← color\\({real}, {real}, {real})") { v: String, x: Double, y: Double, z: Double -> tuples[v] = color(x, y, z) }

        When("{var} ← normalize\\({var})") { v1: String, v2: String -> tuples[v1] = tuples[v2]!!.normalize() }

        Then("{var}.x = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.x).isCloseTo(e, epsilon) }
        Then("{var}.y = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.y).isCloseTo(e, epsilon) }
        Then("{var}.z = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.z).isCloseTo(e, epsilon) }
        Then("{var}.w = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.w).isCloseTo(e, epsilon) }

        Then("{var} is a point")      { v: String -> assertThat(tuples[v]!!.point).isTrue() }
        Then("{var} is not a point")  { v: String -> assertThat(tuples[v]!!.point).isFalse() }
        Then("{var} is a vector")     { v: String -> assertThat(tuples[v]!!.vector).isTrue() }
        Then("{var} is not a vector") { v: String -> assertThat(tuples[v]!!.vector).isFalse() }

        Then("{var} = tuple\\({real}, {real}, {real}, {real})") { v: String, x: Double, y: Double, z: Double, w: Double ->
            assertThat(tuples[v]!!).isCloseTo(Tuple(x, y, z, w), epsilon)
        }

        Then("{var} + {var} = tuple\\({real}, {real}, {real}, {real})") { v1: String, v2: String, x: Double, y: Double, z: Double, w: Double ->
            assertThat(tuples[v1]!! + tuples[v2]!!).isCloseTo(Tuple(x, y, z, w), epsilon)
        }

        Then("{var} - {var} = tuple\\({real}, {real}, {real}, {real})") { v1: String, v2: String, x: Double, y: Double, z: Double, w: Double ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(Tuple(x, y, z, w), epsilon)
        }

        Then("{var} - {var} = point\\({real}, {real}, {real})") { v1: String, v2: String, x: Double, y: Double, z: Double ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(point(x, y, z), epsilon)
        }

        Then("{var} - {var} = vector\\({real}, {real}, {real})") { v1: String, v2: String, x: Double, y: Double, z: Double ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(vector(x, y, z), epsilon)
        }

        Then("-{var} = tuple\\({real}, {real}, {real}, {real})") { v: String, x: Double, y: Double, z: Double, w: Double ->
            assertThat(-tuples[v]!!).isCloseTo(Tuple(x, y, z, w), epsilon)
        }

        Then("-{var} = vector\\({real}, {real}, {real})") { v: String, x: Double, y: Double, z: Double ->
            assertThat(-tuples[v]!!).isCloseTo(vector(x, y, z), epsilon)
        }

        Then("{var} * {real} = tuple\\({real}, {real}, {real}, {real})") {
                v: String, s: Double, x: Double, y: Double, z: Double, w: Double ->
            assertThat(tuples[v]!! * s).isCloseTo(Tuple(x, y, z, w), epsilon)
        }

        Then("{var} / {real} = tuple\\({real}, {real}, {real}, {real})") {
                v: String, s: Double, x: Double, y: Double, z: Double, w: Double ->
            assertThat(tuples[v]!! / s).isCloseTo(Tuple(x, y, z, w), epsilon)
        }

        Then("magnitude\\({var}) = {real}") { v: String, e: Double ->
            assertThat(tuples[v]!!.magnitude).isCloseTo(e, epsilon)
        }

        Then("normalize\\({var}) =( approximately) vector\\({real}, {real}, {real})") {
                v: String, x: Double, y: Double, z: Double ->
            assertThat(tuples[v]!!.normalize()).isCloseTo(vector(x, y, z), epsilon)
        }

        Then("dot\\({var}, {var}) = {real}") { v1: String, v2: String, e: Double ->
            assertThat(tuples[v1]!!.dot(tuples[v2]!!)).isCloseTo(e, epsilon)
        }

        Then("cross\\({var}, {var}) = vector\\({real}, {real}, {real})") {
                v1: String, v2: String, x: Double, y: Double, z: Double ->
            assertThat(tuples[v1]!!.cross(tuples[v2]!!)).isCloseTo(vector(x, y, z), epsilon)
        }

        Then("{var}.red = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.r).isCloseTo(e, epsilon) }
        Then("{var}.green = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.g).isCloseTo(e, epsilon) }
        Then("{var}.blue = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.b).isCloseTo(e, epsilon) }

        Then("{var} + {var} = color\\({real}, {real}, {real})") {
                v1: String, v2: String, r: Double, g: Double, b: Double ->
            assertThat(tuples[v1]!! + tuples[v2]!!).isCloseTo(color(r, g, b), epsilon)
        }

        Then("{var} - {var} = color\\({real}, {real}, {real})") {
                v1: String, v2: String, r: Double, g: Double, b: Double ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(color(r, g, b), epsilon)
        }

        Then("{var} * {real} = color\\({real}, {real}, {real})") {
                v: String, s: Double, r: Double, g: Double, b: Double ->
            assertThat(tuples[v]!! * s).isCloseTo(color(r, g, b), epsilon)
        }

        Then("{var} * {var} = color\\({real}, {real}, {real})") {
                v1: String, v2: String, r: Double, g: Double, b: Double ->
            assertThat(tuples[v1]!! * tuples[v2]!!).isCloseTo(color(r, g, b), epsilon)
        }
    }
}

private fun Assert<Tuple>.isCloseTo(expected: Tuple, epsilon: Double) {
    matchesPredicate { v : Tuple -> v.isCloseTo(expected, epsilon) }
}
