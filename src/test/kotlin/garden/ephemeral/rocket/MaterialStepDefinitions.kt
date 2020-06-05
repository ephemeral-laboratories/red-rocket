package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.LightStepDefinitions.Companion.light
import garden.ephemeral.rocket.Tuple.Companion.grey
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

        Given("material.color ← {color}") { v: Tuple ->
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
                reflective = v
            }
        }

        Given("material.pattern ← stripe_pattern\\({color}, {color})") { a: Tuple, b: Tuple ->
            material = material.build {
                pattern = StripePattern(a, b)
            }
        }

        Given("in_shadow ← {boolean}") { v: Boolean -> inShadow = v }

        When("{tuple_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var})") {
                tv1: String, tv2: String, tv3: String, tv4: String ->
            tuples[tv1] = material.lighting(Sphere(), light, tuples[tv2]!!, tuples[tv3]!!, tuples[tv4]!!, false)
        }
        When("{tuple_var} ← lighting\\(material, light, {tuple_var}, {tuple_var}, {tuple_var}, in_shadow)") {
                tv1: String, tv2: String, tv3: String, tv4: String ->
            tuples[tv1] = material.lighting(Sphere(), light, tuples[tv2]!!, tuples[tv3]!!, tuples[tv4]!!, inShadow)
        }
        When("{tuple_var} ← lighting\\(material, light, {point}, {tuple_var}, {tuple_var}, {boolean})") {
                tv1: String, p: Tuple, tv2: String, tv3: String, b: Boolean ->
            tuples[tv1] = material.lighting(Sphere(), light, p, tuples[tv2]!!, tuples[tv3]!!, b)
        }

        Then("^material = material\\(\\)") { assertThat(material).isEqualTo(Material.default) }

        Then("material.color = {color}")           { e: Tuple -> assertThat(material.color).isEqualTo(e) }
        Then("material.ambient = {real}")          { e: Double -> assertThat(material.ambient).isEqualTo(grey(e)) }
        Then("material.diffuse = {real}")          { e: Double -> assertThat(material.diffuse).isEqualTo(grey(e)) }
        Then("material.specular = {real}")         { e: Double -> assertThat(material.specular).isEqualTo(grey(e)) }
        Then("material.shininess = {real}")        { e: Double -> assertThat(material.shininess).isEqualTo(e) }
        Then("material.reflective = {real}")       { e: Double -> assertThat(material.reflective).isEqualTo(e) }
        Then("material.transparency = {real}")     { e: Double -> assertThat(material.transparency).isEqualTo(e) }
        Then("material.refractive_index = {real}") { e: Double -> assertThat(material.refractiveIndex).isEqualTo(e) }
    }
}