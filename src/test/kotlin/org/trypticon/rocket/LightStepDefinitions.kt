package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples

class LightStepDefinitions: En {
    companion object {
        lateinit var light: PointLight
    }
    init {
        Given("light ← point_light\\({point}, {color})") { p: Tuple, c: Tuple ->
            light = PointLight(p, c)
        }
        Given("light ← point_light\\({tuple_var}, {tuple_var})") { tv1: String, tv2: String ->
            light = PointLight(tuples[tv1]!!, tuples[tv2]!!)
        }

        Then("light.position = {tuple_var}") { tv: String ->
            assertThat(light.position).isEqualTo(tuples[tv]!!)
        }
        Then("light.intensity = {tuple_var}") { tv: String ->
            assertThat(light.intensity).isEqualTo(tuples[tv]!!)
        }
    }
}