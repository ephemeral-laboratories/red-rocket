
import CommonParameterTypes.Companion.realFromString
import CommonParameterTypes.Companion.realRegex
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
        ParameterType("tuple_var", "(?:a|b|c|t|p|v)\\d*|red|origin|direction|zero|norm") { string -> string }

        ParameterType("tuple", "tuple\\(($realRegex), ($realRegex), ($realRegex), ($realRegex)\\)") {
                s1: String, s2: String, s3: String, s4: String ->
            Tuple(realFromString(s1), realFromString(s2), realFromString(s3), realFromString(s4))
        }

        ParameterType("point", "point\\(($realRegex), ($realRegex), ($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            point(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        ParameterType("vector", "vector\\(($realRegex), ($realRegex), ($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            vector(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        ParameterType("color", "color\\(($realRegex), ($realRegex), ($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            color(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        Given("{tuple_var} ← {tuple}") { v: String, e: Tuple -> tuples[v] = e }
        Given("{tuple_var} ← {point}") { v: String, e: Tuple -> tuples[v] = e }
        Given("{tuple_var} ← {vector}") { v: String, e: Tuple -> tuples[v] = e }
        Given("{tuple_var} ← {color}") { v: String, e: Tuple -> tuples[v] = e }

        When("{tuple_var} ← normalize\\({tuple_var})") { v1: String, v2: String -> tuples[v1] = tuples[v2]!!.normalize() }

        Then("{tuple_var}.x = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.x).isCloseTo(e, epsilon) }
        Then("{tuple_var}.y = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.y).isCloseTo(e, epsilon) }
        Then("{tuple_var}.z = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.z).isCloseTo(e, epsilon) }
        Then("{tuple_var}.w = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.w).isCloseTo(e, epsilon) }

        Then("{tuple_var} is a point")      { v: String -> assertThat(tuples[v]!!.point).isTrue() }
        Then("{tuple_var} is not a point")  { v: String -> assertThat(tuples[v]!!.point).isFalse() }
        Then("{tuple_var} is a vector")     { v: String -> assertThat(tuples[v]!!.vector).isTrue() }
        Then("{tuple_var} is not a vector") { v: String -> assertThat(tuples[v]!!.vector).isFalse() }

        Then("{tuple_var} = {tuple}") { v: String, e: Tuple -> assertThat(tuples[v]!!).isCloseTo(e, epsilon) }
        Then("{tuple_var} = {point}") { v: String, e: Tuple -> assertThat(tuples[v]!!).isCloseTo(e, epsilon) }
        Then("{tuple_var} = {vector}") { v: String, e: Tuple -> assertThat(tuples[v]!!).isCloseTo(e, epsilon) }

        Then("{tuple_var} + {tuple_var} = {tuple}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!! + tuples[v2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {tuple}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {point}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {vector}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(e, epsilon)
        }

        Then("-{tuple_var} = {tuple}") { v: String, e: Tuple ->
            assertThat(-tuples[v]!!).isCloseTo(e, epsilon)
        }

        Then("-{tuple_var} = {vector}") { v: String, e: Tuple ->
            assertThat(-tuples[v]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} * {real} = {tuple}") { v: String, s: Double, t: Tuple ->
            assertThat(tuples[v]!! * s).isCloseTo(t, epsilon)
        }

        Then("{tuple_var} / {real} = {tuple}") { v: String, s: Double, e: Tuple ->
            assertThat(tuples[v]!! / s).isCloseTo(e, epsilon)
        }

        Then("magnitude\\({tuple_var}) = {real}") { v: String, e: Double ->
            assertThat(tuples[v]!!.magnitude).isCloseTo(e, epsilon)
        }

        Then("normalize\\({tuple_var}) =( approximately) {vector}") { v: String, e: Tuple ->
            assertThat(tuples[v]!!.normalize()).isCloseTo(e, epsilon)
        }

        Then("dot\\({tuple_var}, {tuple_var}) = {real}") { v1: String, v2: String, e: Double ->
            assertThat(tuples[v1]!!.dot(tuples[v2]!!)).isCloseTo(e, epsilon)
        }

        Then("cross\\({tuple_var}, {tuple_var}) = {vector}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!!.cross(tuples[v2]!!)).isCloseTo(e, epsilon)
        }

        Then("{tuple_var}.red = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.r).isCloseTo(e, epsilon) }
        Then("{tuple_var}.green = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.g).isCloseTo(e, epsilon) }
        Then("{tuple_var}.blue = {real}") { v: String, e: Double -> assertThat(tuples[v]!!.b).isCloseTo(e, epsilon) }

        Then("{tuple_var} + {tuple_var} = {color}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!! + tuples[v2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {color}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!! - tuples[v2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} * {real} = {color}") { v: String, s: Double, e: Tuple ->
            assertThat(tuples[v]!! * s).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} * {tuple_var} = {color}") { v1: String, v2: String, e: Tuple ->
            assertThat(tuples[v1]!! * tuples[v2]!!).isCloseTo(e, epsilon)
        }
    }
}

fun Assert<Tuple>.isCloseTo(expected: Tuple, delta: Double) {
    matchesPredicate { t : Tuple -> t.isCloseTo(expected, delta) }
}
