
import LightStepDefinitions.Companion.light
import Lighting.Companion.lighting
import MaterialStepDefinitions.Companion.m
import TupleStepDefinitions.Companion.tuples
import assertk.assertThat
import io.cucumber.java8.En

class LightingStepDefinitions: En {
    private val epsilon: Double = 0.00001

    companion object {
        lateinit var result: Tuple
    }

    init {
        When("result ← lighting\\(m, light, {tuple_var}, {tuple_var}, {tuple_var})") {
                tv1: String, tv2: String, tv3: String ->
            result = lighting(m, light, tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!)
        }

        Then("result = {color}") { e: Tuple ->
            assertThat(result).isCloseTo(e, epsilon)
        }
    }
}