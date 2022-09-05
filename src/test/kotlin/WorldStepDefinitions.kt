package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.Transforms.Companion.scaling
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import garden.ephemeral.rocket.color.Color.Companion.white
import garden.ephemeral.rocket.shapes.Sphere
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class WorldStepDefinitions(universe: Universe) : En {
    init {
        Given("^world ← world\\(\\)") {
            universe.world = World()
        }
        Given("^world ← default_world\\(\\)") {
            universe.world = defaultWorld()
        }

        Given("world.light ← point_light\\({point}, {color})") { p: Tuple, c: Color ->
            universe.world.lights = mutableListOf(PointLight(p, c))
        }

        Given("{shape_var} is added to world") { sv: String ->
            universe.world.objects.add(universe.shapes[sv]!!)
        }
        Given("{shape_var} ← the first object in world") { sv: String ->
            universe.shapes[sv] = universe.world.objects[0]
        }
        Given("{shape_var} ← the second object in world") { sv: String ->
            universe.shapes[sv] = universe.world.objects[1]
        }

        When("intersections ← intersect_world\\(world, {ray_var})") { rv: String ->
            universe.intersections = universe.world.intersect(universe.rays[rv]!!)
        }

        When("{color_var} ← shade_hit\\(world, comps)") { cv: String ->
            universe.colors[cv] = universe.world.shadeHit(universe.comps)
        }
        When("{color_var} ← shade_hit\\(world, comps, {int})") { cv: String, i: Int ->
            universe.colors[cv] = universe.world.shadeHit(universe.comps, i)
        }

        When("{color_var} ← color_at\\(world, {ray_var})") { cv: String, rv: String ->
            universe.colors[cv] = universe.world.colorAt(universe.rays[rv]!!)
        }

        When("{color_var} ← reflected_color\\(world, comps)") { cv: String ->
            universe.colors[cv] = universe.world.reflectedColor(universe.comps)
        }
        When("{color_var} ← reflected_color\\(world, comps, {int})") { cv: String, i: Int ->
            universe.colors[cv] = universe.world.reflectedColor(universe.comps, i)
        }
        When("{color_var} ← refracted_color\\(world, comps, {int})") { cv: String, i: Int ->
            universe.colors[cv] = universe.world.refractedColor(universe.comps, i)
        }

        Then("world contains no objects") { assertThat(universe.world.objects).isEmpty() }
        Then("world contains {shape_var}") { sv: String ->
            assertThat(universe.world.objects).contains(universe.shapes[sv]!!)
        }

        Then("world has no light source") { assertThat(universe.world.lights).isEmpty() }
        Then("world.light = light") {
            assertThat(universe.world.lights).isEqualTo(mutableListOf(universe.light))
        }

        Then("is_shadowed\\(world, {tuple_var}) is {boolean}") { tv: String, e: Boolean ->
            assertThat(universe.world.isShadowed(universe.tuples[tv]!!, universe.world.lights[0])).isEqualTo(e)
        }

        Then("color_at\\(world, {ray_var}) should terminate successfully") { rv: String ->
            universe.world.colorAt(universe.rays[rv]!!)
        }
    }

    private fun defaultWorld(): World {
        return World().apply {
            lights.add(PointLight(point(-10.0, 10.0, -10.0), white))
            objects.add(
                Sphere().apply {
                    material = Material.build {
                        color = linearRgb(0.8, 1.0, 0.6)
                        diffuse = grey(0.7)
                        specular = grey(0.2)
                    }
                }
            )
            objects.add(
                Sphere().apply {
                    transform = scaling(0.5, 0.5, 0.5)
                }
            )
        }
    }
}
