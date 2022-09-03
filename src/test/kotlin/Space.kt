package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.shapes.Shape
import java.nio.file.Path

/**
 * State object to share global state between multiple step definition classes.
 *
 * Often this would be called `World` instead, but we have a class with that name
 * in the system and would prefer not to have the clash.
 */
class Space {
    val tuples = mutableMapOf<String, Tuple>()
    val matrices = mutableMapOf<String, Matrix>()
    val colors = mutableMapOf<String, Color>()
    val rays = mutableMapOf<String, Ray>()
    val namedIntersections = mutableMapOf<String, Intersection>()
    val shapes = mutableMapOf<String, Shape>()
    val files = mutableMapOf<String, Path>()

    lateinit var material: Material
    lateinit var light: PointLight
    lateinit var world: World
    lateinit var comps: Intersection.Precomputed
    lateinit var comps2: Intersection.Precomputed2
    lateinit var intersections: Intersections
    lateinit var canvas: Canvas

    init {
        matrices["identity_matrix"] = Matrix.identity4x4
    }
}
