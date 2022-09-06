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
class WorldStepDefinitions(space: Space) : En {
    init {
        Given("^world ← world\\(\\)") {
            space.world = World()
        }
        Given("^world ← default_world\\(\\)") {
            space.world = defaultWorld()
        }

        Given("world.light ← point_light\\({point}, {color})") { p: Tuple, c: Color ->
            space.world.lights = mutableListOf(PointLight(p, c))
        }

        Given("{shape_var} is added to world") { sv: String ->
            space.world.objects.add(space.shapes[sv]!!)
        }
        Given("{shape_var} ← the first object in world") { sv: String ->
            space.shapes[sv] = space.world.objects[0]
        }
        Given("{shape_var} ← the second object in world") { sv: String ->
            space.shapes[sv] = space.world.objects[1]
        }

        When("intersections ← intersect_world\\(world, {ray_var})") { rv: String ->
            space.intersections = space.world.intersect(space.rays[rv]!!)
        }

        When("{color_var} ← shade_hit\\(world, comps)") { cv: String ->
            space.colors[cv] = space.world.shadeHit(space.comps)
        }
        When("{color_var} ← shade_hit\\(world, comps, {int})") { cv: String, i: Int ->
            space.colors[cv] = space.world.shadeHit(space.comps, i)
        }

        When("{color_var} ← color_at\\(world, {ray_var})") { cv: String, rv: String ->
            space.colors[cv] = space.world.colorAt(space.rays[rv]!!)
        }

        When("{color_var} ← reflected_color\\(world, comps)") { cv: String ->
            space.colors[cv] = space.world.reflectedColor(space.comps)
        }
        When("{color_var} ← reflected_color\\(world, comps, {int})") { cv: String, i: Int ->
            space.colors[cv] = space.world.reflectedColor(space.comps, i)
        }
        When("{color_var} ← refracted_color\\(world, comps, {int})") { cv: String, i: Int ->
            space.colors[cv] = space.world.refractedColor(space.comps, i)
        }

        Then("world contains no objects") { assertThat(space.world.objects).isEmpty() }
        Then("world contains {shape_var}") { sv: String ->
            assertThat(space.world.objects).contains(space.shapes[sv]!!)
        }

        Then("world has no light source") { assertThat(space.world.lights).isEmpty() }
        Then("world.light = light") {
            assertThat(space.world.lights).isEqualTo(mutableListOf(space.light))
        }

        Then("is_shadowed\\(world, {tuple_var}) is {boolean}") { tv: String, e: Boolean ->
            assertThat(space.world.isShadowed(space.tuples[tv]!!, space.world.lights[0])).isEqualTo(e)
        }

        Then("color_at\\(world, {ray_var}) should terminate successfully") { rv: String ->
            space.world.colorAt(space.rays[rv]!!)
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
