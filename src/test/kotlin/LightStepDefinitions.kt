package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.color.Color
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class LightStepDefinitions(space: Space) : En {
    init {
        Given("light ← point_light\\({point}, {color})") { p: Tuple, c: Color ->
            space.light = PointLight(p, c)
        }
        Given("light ← point_light\\({tuple_var}, {color_var})") { tv: String, cv: String ->
            space.light = PointLight(space.tuples[tv]!!, space.colors[cv]!!)
        }

        Then("light.position = {tuple_var}") { tv: String ->
            assertThat(space.light.position).isEqualTo(space.tuples[tv]!!)
        }
        Then("light.intensity = {color_var}") { tv: String ->
            assertThat(space.light.intensityAsColor).isEqualTo(space.colors[tv]!!)
        }
    }
}
