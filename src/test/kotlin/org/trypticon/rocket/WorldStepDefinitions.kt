package org.trypticon.rocket

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.cucumber.java8.En
import org.trypticon.rocket.IntersectionStepDefinitions.Companion.comps
import org.trypticon.rocket.IntersectionStepDefinitions.Companion.xs
import org.trypticon.rocket.LightStepDefinitions.Companion.light
import org.trypticon.rocket.RayStepDefinitions.Companion.rays
import org.trypticon.rocket.TupleStepDefinitions.Companion.tuples
import org.trypticon.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import org.trypticon.rocket.shapes.Sphere

class WorldStepDefinitions: En {
    companion object {
        lateinit var w: World
    }

    init {
        Given("^w ← world\\(\\)") {
            w = World()
        }
        Given("^w ← default_world\\(\\)") {
            w = defaultWorld()
        }

        Given("w.light ← point_light\\({point}, {color})") { p: Tuple, c: Tuple ->
            w.lights = mutableListOf(PointLight(p, c))
        }

        Given("{shape_var} is added to w") { sv: String ->
            w.objects.add(shapes[sv]!!)
        }
        Given("{shape_var} ← the first object in w") { sv: String ->
            shapes[sv] = w.objects[0]
        }
        Given("{shape_var} ← the second object in w") { sv: String ->
            shapes[sv] = w.objects[1]
        }

        When("xs ← intersect_world\\(w, {ray_var})") { rv: String ->
            xs = w.intersect(rays[rv]!!)
        }

        When("{tuple_var} ← shade_hit\\(w, comps)") { tv: String ->
            tuples[tv] = w.shadeHit(comps)
        }
        When("{tuple_var} ← shade_hit\\(w, comps, {int})") { tv: String, i: Int ->
            tuples[tv] = w.shadeHit(comps, i)
        }

        When("{tuple_var} ← color_at\\(w, {ray_var})") { tv: String, rv: String ->
            tuples[tv] = w.colorAt(rays[rv]!!)
        }

        When("{tuple_var} ← reflected_color\\(w, comps)") { tv: String ->
            tuples[tv] = w.reflectedColor(comps)
        }
        When("{tuple_var} ← reflected_color\\(w, comps, {int})") { tv: String, i: Int ->
            tuples[tv] = w.reflectedColor(comps, i)
        }
        When("{tuple_var} ← refracted_color\\(w, comps, {int})") { tv: String, i: Int ->
            tuples[tv] = w.refractedColor(comps, i)
        }

        Then("w contains no objects") { assertThat(w.objects).isEmpty() }
        Then("w contains {shape_var}") { sv: String ->
            assertThat(w.objects).contains(shapes[sv]!!)
        }

        Then("w has no light source") { assertThat(w.lights).isEmpty() }
        Then("w.light = light") {
            assertThat(w.lights).isEqualTo(mutableListOf(light))
        }

        Then("is_shadowed\\(w, {tuple_var}) is {boolean}") { tv: String, e: Boolean ->
            assertThat(w.isShadowed(tuples[tv]!!, w.lights[0])).isEqualTo(e)
        }

        Then("color_at\\(w, {ray_var}) should terminate successfully") { rv: String ->
            w.colorAt(rays[rv]!!)
        }
    }

    private fun defaultWorld(): World {
        return World().apply {
            lights.add(PointLight(Tuple.point(-10.0, 10.0, -10.0), Tuple.white))
            objects.add(Sphere().apply {
                material = Material.build {
                    color = Tuple.color(0.8, 1.0, 0.6)
                    diffuse = 0.7
                    specular = 0.2
                }
            })
            objects.add(Sphere().apply {
                transform = Transforms.scaling(0.5, 0.5, 0.5)
            })
        }
    }
}