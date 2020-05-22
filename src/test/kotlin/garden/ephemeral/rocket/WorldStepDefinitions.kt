package garden.ephemeral.rocket

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.IntersectionStepDefinitions.Companion.comps
import garden.ephemeral.rocket.IntersectionStepDefinitions.Companion.intersections
import garden.ephemeral.rocket.LightStepDefinitions.Companion.light
import garden.ephemeral.rocket.RayStepDefinitions.Companion.rays
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import garden.ephemeral.rocket.shapes.Sphere
import io.cucumber.java8.En

class WorldStepDefinitions: En {
    companion object {
        lateinit var world: World
    }

    init {
        Given("^world ← world\\(\\)") {
            world = World()
        }
        Given("^world ← default_world\\(\\)") {
            world = defaultWorld()
        }

        Given("world.light ← point_light\\({point}, {color})") { p: Tuple, c: Tuple ->
            world.lights = mutableListOf(PointLight(p, c))
        }

        Given("{shape_var} is added to world") { sv: String ->
            world.objects.add(shapes[sv]!!)
        }
        Given("{shape_var} ← the first object in world") { sv: String ->
            shapes[sv] = world.objects[0]
        }
        Given("{shape_var} ← the second object in world") { sv: String ->
            shapes[sv] = world.objects[1]
        }

        When("intersections ← intersect_world\\(world, {ray_var})") { rv: String ->
            intersections = world.intersect(rays[rv]!!)
        }

        When("{tuple_var} ← shade_hit\\(world, comps)") { tv: String ->
            tuples[tv] = world.shadeHit(comps)
        }
        When("{tuple_var} ← shade_hit\\(world, comps, {int})") { tv: String, i: Int ->
            tuples[tv] = world.shadeHit(comps, i)
        }

        When("{tuple_var} ← color_at\\(world, {ray_var})") { tv: String, rv: String ->
            tuples[tv] = world.colorAt(rays[rv]!!)
        }

        When("{tuple_var} ← reflected_color\\(world, comps)") { tv: String ->
            tuples[tv] = world.reflectedColor(comps)
        }
        When("{tuple_var} ← reflected_color\\(world, comps, {int})") { tv: String, i: Int ->
            tuples[tv] = world.reflectedColor(comps, i)
        }
        When("{tuple_var} ← refracted_color\\(world, comps, {int})") { tv: String, i: Int ->
            tuples[tv] = world.refractedColor(comps, i)
        }

        Then("world contains no objects") { assertThat(world.objects).isEmpty() }
        Then("world contains {shape_var}") { sv: String ->
            assertThat(world.objects).contains(shapes[sv]!!)
        }

        Then("world has no light source") { assertThat(world.lights).isEmpty() }
        Then("world.light = light") {
            assertThat(world.lights).isEqualTo(mutableListOf(light))
        }

        Then("is_shadowed\\(world, {tuple_var}) is {boolean}") { tv: String, e: Boolean ->
            assertThat(world.isShadowed(tuples[tv]!!, world.lights[0])).isEqualTo(e)
        }

        Then("color_at\\(world, {ray_var}) should terminate successfully") { rv: String ->
            world.colorAt(rays[rv]!!)
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