package org.trypticon.rocket

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.matchesPredicate
import io.cucumber.java8.En
import org.trypticon.rocket.CommonParameterTypes.Companion.realFromString
import org.trypticon.rocket.CommonParameterTypes.Companion.realRegex
import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.Tuple.Companion.color
import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.vector

class TupleStepDefinitions: En {
    companion object {
        val tuples: MutableMap<String, Tuple> = mutableMapOf()
    }

    init {
        ParameterType("tuple_var", "(?:a|b|c|t|n|p|r|v)\\d*|position|origin|direction|" +
                "zero|norm|intensity|eyev|normalv|from|to|up|result|" +
                "red|black|white|color") {
                string ->
            string
        }

        ParameterType("tuple", "tuple\\(($realRegex), ($realRegex), ($realRegex), ($realRegex)\\)") {
                s1: String, s2: String, s3: String, s4: String ->
            Tuple(
                realFromString(s1),
                realFromString(s2),
                realFromString(s3),
                realFromString(s4)
            )
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

        Given("{tuple_var} ← {tuple}")  { tv: String, e: Tuple -> tuples[tv] = e }
        Given("{tuple_var} ← {point}")  { tv: String, e: Tuple -> tuples[tv] = e }
        Given("{tuple_var} ← {vector}") { tv: String, e: Tuple -> tuples[tv] = e }
        Given("{tuple_var} ← {color}")  { tv: String, e: Tuple -> tuples[tv] = e }

        When("{tuple_var} ← normalize\\({tuple_var})") { tv1: String, tv2: String -> tuples[tv1] = tuples[tv2]!!.normalize() }

        When("{tuple_var} ← reflect\\({tuple_var}, {tuple_var})") { tv1: String, tv2: String, tv3: String ->
            tuples[tv1] = tuples[tv2]!!.reflect(
                tuples[tv3]!!)
        }

        Then("{tuple_var}.x = {real}") { tv: String, e: Double -> assertThat(tuples[tv]!!.x).isCloseTo(e, epsilon) }
        Then("{tuple_var}.y = {real}") { tv: String, e: Double -> assertThat(tuples[tv]!!.y).isCloseTo(e, epsilon) }
        Then("{tuple_var}.z = {real}") { tv: String, e: Double -> assertThat(tuples[tv]!!.z).isCloseTo(e, epsilon) }
        Then("{tuple_var}.w = {real}") { tv: String, e: Double -> assertThat(tuples[tv]!!.w).isCloseTo(e, epsilon) }

        Then("{tuple_var} is a point")      { tv: String -> assertThat(tuples[tv]!!.point).isTrue() }
        Then("{tuple_var} is not a point")  { tv: String -> assertThat(tuples[tv]!!.point).isFalse() }
        Then("{tuple_var} is a vector")     { tv: String -> assertThat(tuples[tv]!!.vector).isTrue() }
        Then("{tuple_var} is not a vector") { tv: String -> assertThat(tuples[tv]!!.vector).isFalse() }

        Then("{tuple_var} = {tuple}")  { tv: String, e: Tuple -> assertThat(tuples[tv]!!).isCloseTo(e, epsilon) }
        Then("{tuple_var} = {point}")  { tv: String, e: Tuple -> assertThat(tuples[tv]!!).isCloseTo(e, epsilon) }
        Then("{tuple_var} = {vector}") { tv: String, e: Tuple -> assertThat(tuples[tv]!!).isCloseTo(e, epsilon) }
        Then("{tuple_var} = {color}")  { tv: String, e: Tuple -> assertThat(tuples[tv]!!).isCloseTo(e, epsilon) }

        Then("{tuple_var} = {tuple_var}")  { tv1: String, tv2: String ->
            assertThat(tuples[tv1]!!).isCloseTo(tuples[tv2]!!, epsilon)
        }

        Then("{tuple_var} + {tuple_var} = {tuple}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(tuples[tv1]!! + tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {tuple}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(tuples[tv1]!! - tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {point}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(tuples[tv1]!! - tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {vector}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(tuples[tv1]!! - tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("-{tuple_var} = {tuple}") { tv: String, e: Tuple ->
            assertThat(-tuples[tv]!!).isCloseTo(e, epsilon)
        }

        Then("-{tuple_var} = {vector}") { tv: String, e: Tuple ->
            assertThat(-tuples[tv]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} * {real} = {tuple}") { tv: String, s: Double, t: Tuple ->
            assertThat(tuples[tv]!! * s).isCloseTo(t, epsilon)
        }

        Then("{tuple_var} / {real} = {tuple}") { tv: String, s: Double, e: Tuple ->
            assertThat(tuples[tv]!! / s).isCloseTo(e, epsilon)
        }

        Then("magnitude\\({tuple_var}) = {real}") { tv: String, e: Double ->
            assertThat(tuples[tv]!!.magnitude).isCloseTo(e, epsilon)
        }

        Then("normalize\\({tuple_var}) =( approximately) {vector}") { tv: String, e: Tuple ->
            assertThat(tuples[tv]!!.normalize()).isCloseTo(e, epsilon)
        }

        Then("dot\\({tuple_var}, {tuple_var}) = {real}") { tv1: String, tv2: String, e: Double ->
            assertThat(
                tuples[tv1]!!.dot(
                    tuples[tv2]!!)).isCloseTo(e, epsilon)
        }

        Then("cross\\({tuple_var}, {tuple_var}) = {vector}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(
                tuples[tv1]!!.cross(
                    tuples[tv2]!!)).isCloseTo(e, epsilon)
        }

        Then("{tuple_var}.red = {real}") { tv: String, e: Double -> assertThat(tuples[tv]!!.r).isCloseTo(e, epsilon) }
        Then("{tuple_var}.green = {real}") { tv: String, e: Double -> assertThat(tuples[tv]!!.g).isCloseTo(e, epsilon) }
        Then("{tuple_var}.blue = {real}") { tv: String, e: Double -> assertThat(tuples[tv]!!.b).isCloseTo(e, epsilon) }

        Then("{tuple_var} + {tuple_var} = {color}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(tuples[tv1]!! + tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {color}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(tuples[tv1]!! - tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} * {real} = {color}") { tv: String, s: Double, e: Tuple ->
            assertThat(tuples[tv]!! * s).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} * {tuple_var} = {color}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(tuples[tv1]!! * tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} = normalize\\({tuple_var})") { tv1: String, tv2: String ->
            assertThat(tuples[tv1]!!).isCloseTo(
                tuples[tv2]!!.normalize(), epsilon)
        }
    }
}

fun Assert<Tuple>.isCloseTo(expected: Tuple, delta: Double) {
    matchesPredicate { t : Tuple -> t.isCloseTo(expected, delta) }
}
