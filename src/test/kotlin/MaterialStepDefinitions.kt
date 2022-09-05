package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.patterns.StripePattern
import garden.ephemeral.rocket.shapes.Sphere
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class MaterialStepDefinitions(universe: Universe) : En {
    private var inShadow = false

    init {
        Given("^material ← material\\(\\)") {
            universe.material = Material.default
        }

        Given("material.color ← {color}") { v: Color ->
            universe.material = universe.material.build {
                color = v
            }
        }
        Given("material.ambient ← {real}") { v: Double ->
            universe.material = universe.material.build {
                ambient = grey(v)
            }
        }
        Given("material.diffuse ← {real}") { v: Double ->
            universe.material = universe.material.build {
                diffuse = grey(v)
            }
        }
        Given("material.specular ← {real}") { v: Double ->
            universe.material = universe.material.build {
                specular = grey(v)
            }
        }
        Given("material.shininess ← {real}") { v: Double ->
            universe.material = universe.material.build {
                shininess = v
            }
        }
        Given("material.reflective ← {real}") { v: Double ->
            universe.material = universe.material.build {
                reflective = grey(v)
            }
        }

        Given("material.pattern ← stripe_pattern\\({color}, {color})") { a: Color, b: Color ->
            universe.material = universe.material.build {
                pattern = StripePattern(a, b)
            }
        }

        Given("in_shadow ← {boolean}") { v: Boolean -> inShadow = v }

        When(
            "{color_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var})"
        ) { cv: String, tv1: String, tv2: String, tv3: String ->
            universe.colors[cv] = universe.material.lighting(
                Sphere(),
                universe.light,
                universe.tuples[tv1]!!,
                universe.tuples[tv2]!!,
                universe.tuples[tv3]!!,
                false
            )
        }
        When(
            "{color_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var}, in_shadow)"
        ) { cv: String, tv1: String, tv2: String, tv3: String ->
            universe.colors[cv] = universe.material.lighting(
                Sphere(),
                universe.light,
                universe.tuples[tv1]!!,
                universe.tuples[tv2]!!,
                universe.tuples[tv3]!!,
                inShadow
            )
        }
        When(
            "{color_var} ← lighting\\(material, light, {point}, {tuple_var}, {tuple_var}, {boolean})"
        ) { cv: String, p: Tuple, tv1: String, tv2: String, b: Boolean ->
            universe.colors[cv] = universe.material.lighting(
                Sphere(),
                universe.light,
                p,
                universe.tuples[tv1]!!,
                universe.tuples[tv2]!!,
                b
            )
        }

        Then("^material = material\\(\\)") {
            assertThat(universe.material).isEqualTo(Material.default)
        }

        Then("material.color = {color}") { e: Color ->
            assertThat(universe.material.color).isEqualTo(e)
        }

        Then("material.ambient = {real}") { e: Double ->
            assertThat(universe.material.ambient).isEqualTo(grey(e))
        }

        Then("material.ambient = {color}") { e: Color ->
            assertThat(universe.material.ambient).isEqualTo(e)
        }

        Then("material.diffuse = {real}") { e: Double ->
            assertThat(universe.material.diffuse).isEqualTo(grey(e))
        }

        Then("material.diffuse = {color}") { e: Color ->
            assertThat(universe.material.diffuse).isEqualTo(e)
        }

        Then("material.specular = {real}") { e: Double ->
            assertThat(universe.material.specular).isEqualTo(grey(e))
        }

        Then("material.specular = {color}") { e: Color ->
            assertThat(universe.material.specular).isEqualTo(e)
        }

        Then("material.shininess = {real}") { e: Double ->
            assertThat(universe.material.shininess).isEqualTo(e)
        }

        Then("material.reflective = {real}") { e: Double ->
            assertThat(universe.material.reflective).isEqualTo(grey(e))
        }

        Then("material.reflective = {color}") { e: Color ->
            assertThat(universe.material.reflective).isEqualTo(e)
        }

        Then("material.transparency = {real}") { e: Double ->
            assertThat(universe.material.transparency).isEqualTo(grey(e))
        }

        Then("material.transparency = {color}") { e: Color ->
            assertThat(universe.material.transparency).isEqualTo(e)
        }

        Then("material.refractive_index = {real}") { e: Double ->
            assertThat(universe.material.refractiveIndex).isEqualTo(e)
        }

        Then("material.dissolve = {real}") { e: Double ->
            assertThat(universe.material.dissolve).isEqualTo(e)
        }

        Then("material.illumination_model = {int}") { e: Int ->
            assertThat(universe.material.illuminationModel).isEqualTo(e)
        }

        Then("material.emission = {color}") { e: Color ->
            assertThat(universe.material.emission).isEqualTo(e)
        }
    }
}
