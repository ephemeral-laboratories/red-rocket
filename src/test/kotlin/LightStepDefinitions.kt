package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.color.ColorStepDefinitions.Companion.colors
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.color.Color
import io.cucumber.java8.En

class LightStepDefinitions: En {
    companion object {
        lateinit var light: PointLight
    }
    init {
        Given("light ← point_light\\({point}, {color})") { p: Tuple, c: Color ->
            light = PointLight(p, c)
        }
        Given("light ← point_light\\({tuple_var}, {color_var})") { tv: String, cv: String ->
            light = PointLight(tuples[tv]!!, colors[cv]!!)
        }

        Then("light.position = {tuple_var}") { tv: String ->
            assertThat(light.position).isEqualTo(tuples[tv]!!)
        }
        Then("light.intensity = {color_var}") { tv: String ->
            assertThat(light.intensity).isEqualTo(colors[tv]!!)
        }
    }
}