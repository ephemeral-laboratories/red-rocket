package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.LightStepDefinitions.Companion.light
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples

class MaterialStepDefinitions: En {
    companion object {
        lateinit var m: Material
        lateinit var result: Tuple
    }

    init {
        Given("^m ← material\\(\\)") {
            m = Material()
        }

        Given("m.color ← {color}")    { v: Tuple -> m.color = v }
        Given("m.ambient ← {real}")   { v: Double -> m.ambient = v }
        Given("m.diffuse ← {real}")   { v: Double -> m.diffuse = v }
        Given("m.specular ← {real}")  { v: Double -> m.specular = v }
        Given("m.shininess ← {real}") { v: Double -> m.shininess = v }

        When("{tuple_var} ← lighting\\(m, light, {tuple_var}, {tuple_var}, {tuple_var})") {
                tv1: String, tv2: String, tv3: String, tv4: String ->
            tuples[tv1] = m.lighting(light, tuples[tv2]!!, tuples[tv3]!!, tuples[tv4]!!)
        }

        Then("^m = material\\(\\)") { assertThat(m).isEqualTo(Material()) }

        Then("m.color = {color}")    { e: Tuple -> assertThat(m.color).isEqualTo(e) }
        Then("m.ambient = {real}")   { e: Double -> assertThat(m.ambient).isEqualTo(e) }
        Then("m.diffuse = {real}")   { e: Double -> assertThat(m.diffuse).isEqualTo(e) }
        Then("m.specular = {real}")  { e: Double -> assertThat(m.specular).isEqualTo(e) }
        Then("m.shininess = {real}") { e: Double -> assertThat(m.shininess).isEqualTo(e) }
    }
}