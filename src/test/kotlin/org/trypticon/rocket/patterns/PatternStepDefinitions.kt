package org.trypticon.rocket.patterns

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.Matrix
import org.trypticon.rocket.MatrixStepDefinitions.Companion.matrices
import org.trypticon.rocket.ShapeStepDefinitions.Companion.shapes
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples

class PatternStepDefinitions: En {
    companion object {
        lateinit var pattern: Pattern
    }

    init {
        Given("^pattern ← test_pattern\\(\\)") { pattern =
            TestPattern()
        }

        Given("pattern ← stripe_pattern\\({tuple_var}, {tuple_var})") { tv1: String, tv2: String ->
            pattern = StripePattern(tuples[tv1]!!, tuples[tv2]!!)
        }
        Given("pattern ← gradient_pattern\\({tuple_var}, {tuple_var})") { tv1: String, tv2: String ->
            pattern = GradientPattern(tuples[tv1]!!, tuples[tv2]!!)
        }
        Given("pattern ← ring_pattern\\({tuple_var}, {tuple_var})") { tv1: String, tv2: String ->
            pattern = RingPattern(tuples[tv1]!!, tuples[tv2]!!)
        }
        Given("pattern ← checkers_pattern\\({tuple_var}, {tuple_var})") { tv1: String, tv2: String ->
            pattern = CheckersPattern(tuples[tv1]!!, tuples[tv2]!!)
        }

        Given("set_pattern_transform\\(pattern, {translation})") { t: Matrix ->
            pattern.transform = t
        }
        Given("set_pattern_transform\\(pattern, {scaling})") { t: Matrix ->
            pattern.transform = t
        }

        When("{tuple_var} ← stripe_at_object\\(pattern, {shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = pattern.patternAtShape(shapes[sv]!!, p)
        }
        When("{tuple_var} ← pattern_at_shape\\(pattern, {shape_var}, {point})") { tv: String, sv: String, p: Tuple ->
            tuples[tv] = pattern.patternAtShape(shapes[sv]!!, p)
        }

        Then("pattern.transform = {translation}") { t: Matrix -> assertThat(
            pattern.transform).isEqualTo(t) }

        Then("pattern.transform = {matrix_var}") { mv: String ->
            assertThat(pattern.transform).isEqualTo(matrices[mv])
        }

        Then("pattern.a = {tuple_var}") { tv: String ->
            assertThat((pattern as StripePattern).a).isEqualTo(tuples[tv]!!)
        }
        Then("pattern.b = {tuple_var}") { tv: String ->
            assertThat((pattern as StripePattern).b).isEqualTo(tuples[tv]!!)
        }

        Then("stripe_at\\(pattern, {point}) = {tuple_var}") { p: Tuple, tv: String ->
            assertThat(pattern.patternAt(p)).isEqualTo(tuples[tv]!!)
        }
        Then("pattern_at\\(pattern, {point}) = {color}") { p: Tuple, e: Tuple ->
            assertThat(pattern.patternAt(p)).isEqualTo(e)
        }
        Then("pattern_at\\(pattern, {point}) = {tuple_var}") { p: Tuple, tv: String ->
            assertThat(pattern.patternAt(p)).isEqualTo(tuples[tv]!!)
        }
    }
}