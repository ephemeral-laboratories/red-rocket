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
class MaterialStepDefinitions(space: Space) : En {
    private var inShadow = false

    init {
        Given("^material ← material\\(\\)") {
            space.material = Material.default
        }

        Given("material.color ← {color}") { v: Color ->
            space.material = space.material.build {
                color(v)
            }
        }
        Given("material.ambient ← {real}") { v: Double ->
            space.material = space.material.build {
                ambient(grey(v))
            }
        }
        Given("material.diffuse ← {real}") { v: Double ->
            space.material = space.material.build {
                diffuse(grey(v))
            }
        }
        Given("material.specular ← {real}") { v: Double ->
            space.material = space.material.build {
                specular(grey(v))
            }
        }
        Given("material.shininess ← {real}") { v: Double ->
            space.material = space.material.build {
                shininess(v)
            }
        }
        Given("material.reflective ← {real}") { v: Double ->
            space.material = space.material.build {
                reflective(grey(v))
            }
        }

        Given("material.pattern ← stripe_pattern\\({color}, {color})") { a: Color, b: Color ->
            space.material = space.material.build {
                pattern(StripePattern(a, b))
            }
        }

        Given("in_shadow ← {boolean}") { v: Boolean -> inShadow = v }

        When(
            "{color_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var})"
        ) { cv: String, tv1: String, tv2: String, tv3: String ->
            space.colors[cv] = space.material.lighting(
                Sphere(),
                space.light,
                space.tuples[tv1]!!,
                space.tuples[tv2]!!,
                space.tuples[tv3]!!,
                false
            )
        }
        When(
            "{color_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var}, in_shadow)"
        ) { cv: String, tv1: String, tv2: String, tv3: String ->
            space.colors[cv] = space.material.lighting(
                Sphere(),
                space.light,
                space.tuples[tv1]!!,
                space.tuples[tv2]!!,
                space.tuples[tv3]!!,
                inShadow
            )
        }
        When(
            "{color_var} ← lighting\\(material, light, {point}, {tuple_var}, {tuple_var}, {boolean})"
        ) { cv: String, p: Tuple, tv1: String, tv2: String, b: Boolean ->
            space.colors[cv] = space.material.lighting(
                Sphere(),
                space.light,
                p,
                space.tuples[tv1]!!,
                space.tuples[tv2]!!,
                b
            )
        }

        Then("^material = material\\(\\)") {
            assertThat(space.material).isEqualTo(Material.default)
        }

        Then("material.color = {color}") { e: Color ->
            assertThat(space.material.color).isEqualTo(e)
        }

        Then("material.ambient = {real}") { e: Double ->
            assertThat(space.material.ambient).isEqualTo(grey(e))
        }

        Then("material.ambient = {color}") { e: Color ->
            assertThat(space.material.ambient).isEqualTo(e)
        }

        Then("material.diffuse = {real}") { e: Double ->
            assertThat(space.material.diffuse).isEqualTo(grey(e))
        }

        Then("material.diffuse = {color}") { e: Color ->
            assertThat(space.material.diffuse).isEqualTo(e)
        }

        Then("material.specular = {real}") { e: Double ->
            assertThat(space.material.specular).isEqualTo(grey(e))
        }

        Then("material.specular = {color}") { e: Color ->
            assertThat(space.material.specular).isEqualTo(e)
        }

        Then("material.shininess = {real}") { e: Double ->
            assertThat(space.material.shininess).isEqualTo(e)
        }

        Then("material.reflective = {real}") { e: Double ->
            assertThat(space.material.reflective).isEqualTo(grey(e))
        }

        Then("material.reflective = {color}") { e: Color ->
            assertThat(space.material.reflective).isEqualTo(e)
        }

        Then("material.transparency = {real}") { e: Double ->
            assertThat(space.material.transparency).isEqualTo(grey(e))
        }

        Then("material.transparency = {color}") { e: Color ->
            assertThat(space.material.transparency).isEqualTo(e)
        }

        Then("material.refractive_index = {real}") { e: Double ->
            assertThat(space.material.refractiveIndex).isEqualTo(e)
        }

        Then("material.dissolve = {real}") { e: Double ->
            assertThat(space.material.dissolve).isEqualTo(e)
        }

        Then("material.illumination_model = {int}") { e: Int ->
            assertThat(space.material.illuminationModel).isEqualTo(e)
        }

        Then("material.emission = {color}") { e: Color ->
            assertThat(space.material.emission).isEqualTo(e)
        }
    }
}
