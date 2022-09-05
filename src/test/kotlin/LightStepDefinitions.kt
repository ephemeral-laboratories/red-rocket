package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.color.Color
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class LightStepDefinitions(universe: Universe) : En {
    init {
        Given("light ← point_light\\({point}, {color})") { p: Tuple, c: Color ->
            universe.light = PointLight(p, c)
        }
        Given("light ← point_light\\({tuple_var}, {color_var})") { tv: String, cv: String ->
            universe.light = PointLight(universe.tuples[tv]!!, universe.colors[cv]!!)
        }

        Then("light.position = {tuple_var}") { tv: String ->
            assertThat(universe.light.position).isEqualTo(universe.tuples[tv]!!)
        }
        Then("light.intensity = {color_var}") { tv: String ->
            assertThat(universe.light.intensity).isEqualTo(universe.colors[tv]!!)
        }
    }
}
