package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Color.Companion.grey
import garden.ephemeral.rocket.ColorStepDefinitions.Companion.colors
import garden.ephemeral.rocket.LightStepDefinitions.Companion.light
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.patterns.StripePattern
import garden.ephemeral.rocket.shapes.Sphere
import io.cucumber.java8.En

class MaterialStepDefinitions: En {
    companion object {
        lateinit var material: Material
        var inShadow: Boolean = false
    }

    init {
        Given("^material ← material\\(\\)") {
            material = Material.default
        }

        Given("material.color ← {color}") { v: Color ->
            material = material.build {
                color = v
            }
        }
        Given("material.ambient ← {real}") { v: Double ->
            material = material.build {
                ambient = grey(v)
            }
        }
        Given("material.diffuse ← {real}") { v: Double ->
            material = material.build {
                diffuse = grey(v)
            }
        }
        Given("material.specular ← {real}") { v: Double ->
            material = material.build {
                specular = grey(v)
            }
        }
        Given("material.shininess ← {real}") { v: Double ->
            material = material.build {
                shininess = v
            }
        }
        Given("material.reflective ← {real}") { v: Double ->
            material = material.build {
                reflective = grey(v)
            }
        }

        Given("material.pattern ← stripe_pattern\\({color}, {color})") { a: Color, b: Color ->
            material = material.build {
                pattern = StripePattern(a, b)
            }
        }

        Given("in_shadow ← {boolean}") { v: Boolean -> inShadow = v }

        When("{color_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var})") {
                cv: String, tv1: String, tv2: String, tv3: String ->
            colors[cv] = material.lighting(Sphere(), light, tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!, false)
        }
        When("{color_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var}, in_shadow)") {
                cv: String, tv1: String, tv2: String, tv3: String ->
            colors[cv] = material.lighting(Sphere(), light, tuples[tv1]!!, tuples[tv2]!!, tuples[tv3]!!, inShadow)
        }
        When("{color_var} ← lighting\\(material, light, {point}, {tuple_var}, {tuple_var}, {boolean})") {
                cv: String, p: Tuple, tv1: String, tv2: String, b: Boolean ->
            colors[cv] = material.lighting(Sphere(), light, p, tuples[tv1]!!, tuples[tv2]!!, b)
        }

        Then("^material = material\\(\\)") {
            assertThat(material).isEqualTo(Material.default)
        }

        Then("material.color = {color}") { e: Color ->
            assertThat(material.color).isEqualTo(e)
        }

        Then("material.ambient = {real}") { e: Double ->
            assertThat(material.ambient).isEqualTo(grey(e))
        }

        Then("material.ambient = {color}") { e: Color ->
            assertThat(material.ambient).isEqualTo(e)
        }

        Then("material.diffuse = {real}") { e: Double ->
            assertThat(material.diffuse).isEqualTo(grey(e))
        }

        Then("material.diffuse = {color}") { e: Color ->
            assertThat(material.diffuse).isEqualTo(e)
        }

        Then("material.specular = {real}") { e: Double ->
            assertThat(material.specular).isEqualTo(grey(e))
        }

        Then("material.specular = {color}") { e: Color ->
            assertThat(material.specular).isEqualTo(e)
        }

        Then("material.shininess = {real}") { e: Double ->
            assertThat(material.shininess).isEqualTo(e)
        }

        Then("material.reflective = {real}") { e: Double ->
            assertThat(material.reflective).isEqualTo(grey(e))
        }

        Then("material.reflective = {color}") { e: Color ->
            assertThat(material.reflective).isEqualTo(e)
        }

        Then("material.transparency = {real}") { e: Double ->
            assertThat(material.transparency).isEqualTo(grey(e))
        }

        Then("material.transparency = {color}") { e: Color ->
            assertThat(material.transparency).isEqualTo(e)
        }

        Then("material.refractive_index = {real}") { e: Double ->
            assertThat(material.refractiveIndex).isEqualTo(e)
        }

        Then("material.dissolve = {real}") { e: Double ->
            assertThat(material.dissolve).isEqualTo(e)
        }

        Then("material.illumination_model = {int}") { e: Int ->
            assertThat(material.illuminationModel).isEqualTo(e)
        }

        Then("material.emission = {color}") { e: Color ->
            assertThat(material.emission).isEqualTo(e)
        }
    }
}