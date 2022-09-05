package garden.ephemeral.rocket

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.matchesPredicate
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En
import kotlin.math.abs

// Constructed reflectively
@Suppress("unused")
class TupleStepDefinitions(universe: Universe) : En {
    init {
        ParameterType(
            "tuple_var",
            "(?:tuple|point|position|vector|origin|direction|reflection|refraction|eyeline|normal|zero" +
                "|from|to|up" +
                "|S" +
                ")\\d?"
        ) { string ->
            string
        }

        ParameterType("tuple", "tuple\\(($realRegex),\\s*($realRegex),\\s*($realRegex),\\s*($realRegex)\\)") {
                s1: String, s2: String, s3: String, s4: String ->
            Tuple(
                realFromString(s1),
                realFromString(s2),
                realFromString(s3),
                realFromString(s4)
            )
        }

        ParameterType("point", "point\\(($realRegex),\\s*($realRegex),\\s*($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            point(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        ParameterType("vector", "vector\\(($realRegex),\\s*($realRegex),\\s*($realRegex)\\)") {
                s1: String, s2: String, s3: String ->
            vector(realFromString(s1), realFromString(s2), realFromString(s3))
        }

        Given("{tuple_var} ← {tuple}") { tv: String, v: Tuple -> universe.tuples[tv] = v }
        Given("{tuple_var} ← {point}") { tv: String, v: Tuple -> universe.tuples[tv] = v }
        Given("{tuple_var} ← {vector}") { tv: String, v: Tuple -> universe.tuples[tv] = v }

        When("{tuple_var} ← normalize\\({vector})") { tv1: String, v: Tuple -> universe.tuples[tv1] = v.normalize() }
        When("{tuple_var} ← normalize\\({tuple_var})") { tv1: String, tv2: String ->
            universe.tuples[tv1] = universe.tuples[tv2]!!.normalize()
        }

        When("{tuple_var} ← reflect\\({tuple_var}, {tuple_var})") { tv1: String, tv2: String, tv3: String ->
            universe.tuples[tv1] = universe.tuples[tv2]!!.reflect(universe.tuples[tv3]!!)
        }

        When("{tuple_var} ← {tuple_var} * {real}") { tupleVar1: String, tupleVar2: String, scale: Double ->
            universe.tuples[tupleVar1] = universe.tuples[tupleVar2]!! * scale
        }

        Then("{tuple_var}.x = {real}") { tv: String, e: Double ->
            assertThat(universe.tuples[tv]!!.x).isCloseTo(e, epsilon)
        }
        Then("{tuple_var}.y = {real}") { tv: String, e: Double ->
            assertThat(universe.tuples[tv]!!.y).isCloseTo(e, epsilon)
        }
        Then("{tuple_var}.z = {real}") { tv: String, e: Double ->
            assertThat(universe.tuples[tv]!!.z).isCloseTo(e, epsilon)
        }
        Then("{tuple_var}.w = {real}") { tv: String, e: Double ->
            assertThat(universe.tuples[tv]!!.w).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} is a point") { tv: String -> assertThat(universe.tuples[tv]!!.isPoint).isTrue() }
        Then("{tuple_var} is not a point") { tv: String -> assertThat(universe.tuples[tv]!!.isPoint).isFalse() }
        Then("{tuple_var} is a vector") { tv: String -> assertThat(universe.tuples[tv]!!.isVector).isTrue() }
        Then("{tuple_var} is not a vector") { tv: String -> assertThat(universe.tuples[tv]!!.isVector).isFalse() }

        Then("{tuple_var} = {tuple}") { tv: String, e: Tuple ->
            assertThat(universe.tuples[tv]!!).isCloseTo(e, epsilon)
        }
        Then("{tuple_var} = {point}") { tv: String, e: Tuple ->
            assertThat(universe.tuples[tv]!!).isCloseTo(e, epsilon)
        }
        Then("{tuple_var} = {vector}") { tv: String, e: Tuple ->
            assertThat(universe.tuples[tv]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} = {tuple_var}") { tv1: String, tv2: String ->
            assertThat(universe.tuples[tv1]!!).isCloseTo(universe.tuples[tv2]!!, epsilon)
        }

        Then("{tuple_var} + {tuple_var} = {tuple}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(universe.tuples[tv1]!! + universe.tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {tuple}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(universe.tuples[tv1]!! - universe.tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {point}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(universe.tuples[tv1]!! - universe.tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} - {tuple_var} = {vector}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(universe.tuples[tv1]!! - universe.tuples[tv2]!!).isCloseTo(e, epsilon)
        }

        Then("-{tuple_var} = {tuple}") { tv: String, e: Tuple ->
            assertThat(-universe.tuples[tv]!!).isCloseTo(e, epsilon)
        }

        Then("-{tuple_var} = {vector}") { tv: String, e: Tuple ->
            assertThat(-universe.tuples[tv]!!).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} * {real} = {tuple}") { tv: String, s: Double, t: Tuple ->
            assertThat(universe.tuples[tv]!! * s).isCloseTo(t, epsilon)
        }

        Then("{tuple_var} \\/ {real} = {tuple}") { tv: String, s: Double, e: Tuple ->
            assertThat(universe.tuples[tv]!! / s).isCloseTo(e, epsilon)
        }

        Then("magnitude\\({tuple_var}) = {real}") { tv: String, e: Double ->
            assertThat(universe.tuples[tv]!!.magnitude).isCloseTo(e, epsilon)
        }

        Then("normalize\\({tuple_var}) =( approximately) {vector}") { tv: String, e: Tuple ->
            assertThat(universe.tuples[tv]!!.normalize()).isCloseTo(e, epsilon)
        }

        Then("dot\\({tuple_var}, {tuple_var}) = {real}") { tv1: String, tv2: String, e: Double ->
            assertThat(universe.tuples[tv1]!!.dot(universe.tuples[tv2]!!)).isCloseTo(e, epsilon)
        }

        Then("cross\\({tuple_var}, {tuple_var}) = {vector}") { tv1: String, tv2: String, e: Tuple ->
            assertThat(universe.tuples[tv1]!!.cross(universe.tuples[tv2]!!)).isCloseTo(e, epsilon)
        }

        Then("{tuple_var} = normalize\\({tuple_var})") { tv1: String, tv2: String ->
            assertThat(universe.tuples[tv1]!!).isCloseTo(universe.tuples[tv2]!!.normalize(), epsilon)
        }
    }
}

fun Assert<Tuple>.isCloseTo(expected: Tuple, delta: Double) {
    matchesPredicate { t: Tuple -> t.isCloseTo(expected, delta) }
}

fun Tuple.isCloseTo(their: Tuple, delta: Double): Boolean {
    return abs(x - their.x) <= delta &&
        abs(y - their.y) <= delta &&
        abs(z - their.z) <= delta &&
        abs(w - their.w) <= delta
}
