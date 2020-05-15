package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.LightStepDefinitions.Companion.light
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples
import org.trypticon.rocket.patterns.StripePattern
import org.trypticon.rocket.shapes.Sphere

class MaterialStepDefinitions: En {
    companion object {
        lateinit var m: Material
        lateinit var result: Tuple
        var inShadow: Boolean = false
    }

    init {
        Given("^m ← material\\(\\)") {
            m = Material.default
        }

        Given("m.color ← {color}") { v: Tuple ->
            m = m.build {
                color = v
            }
        }
        Given("m.ambient ← {real}") { v: Double ->
            m = m.build {
                ambient = v
            }
        }
        Given("m.diffuse ← {real}") { v: Double ->
            m = m.build {
                diffuse = v
            }
        }
        Given("m.specular ← {real}") { v: Double ->
            m = m.build {
                specular = v
            }
        }
        Given("m.shininess ← {real}") { v: Double ->
            m = m.build {
                shininess = v
            }
        }
        Given("m.reflective ← {real}") { v: Double ->
            m = m.build {
                reflective = v
            }
        }

        Given("m.pattern ← stripe_pattern\\({color}, {color})") { a: Tuple, b: Tuple ->
            m = m.build {
                pattern = StripePattern(a, b)
            }
        }

        Given("in_shadow ← {boolean}") { v: Boolean -> inShadow = v }

        When("{tuple_var} ← lighting\\(m, light, {tuple_var}, {tuple_var}, {tuple_var})") {
                tv1: String, tv2: String, tv3: String, tv4: String ->
            tuples[tv1] = m.lighting(Sphere(), light, tuples[tv2]!!, tuples[tv3]!!, tuples[tv4]!!, false)
        }
        When("{tuple_var} ← lighting\\(m, light, {tuple_var}, {tuple_var}, {tuple_var}, in_shadow)") {
                tv1: String, tv2: String, tv3: String, tv4: String ->
            tuples[tv1] = m.lighting(Sphere(), light, tuples[tv2]!!, tuples[tv3]!!, tuples[tv4]!!, inShadow)
        }
        When("{tuple_var} ← lighting\\(m, light, {point}, {tuple_var}, {tuple_var}, {boolean})") {
                tv1: String, p: Tuple, tv2: String, tv3: String, b: Boolean ->
            tuples[tv1] = m.lighting(Sphere(), light, p, tuples[tv2]!!, tuples[tv3]!!, b)
        }

        Then("^m = material\\(\\)") { assertThat(m).isEqualTo(Material.default) }

        Then("m.color = {color}")           { e: Tuple -> assertThat(m.color).isEqualTo(e) }
        Then("m.ambient = {real}")          { e: Double -> assertThat(m.ambient).isEqualTo(e) }
        Then("m.diffuse = {real}")          { e: Double -> assertThat(m.diffuse).isEqualTo(e) }
        Then("m.specular = {real}")         { e: Double -> assertThat(m.specular).isEqualTo(e) }
        Then("m.shininess = {real}")        { e: Double -> assertThat(m.shininess).isEqualTo(e) }
        Then("m.reflective = {real}")       { e: Double -> assertThat(m.reflective).isEqualTo(e) }
        Then("m.transparency = {real}")     { e: Double -> assertThat(m.transparency).isEqualTo(e) }
        Then("m.refractive_index = {real}") { e: Double -> assertThat(m.refractiveIndex).isEqualTo(e) }
    }
}