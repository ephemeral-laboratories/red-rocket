package garden.ephemeral.rocket

import garden.ephemeral.rocket.Color.Companion.black
import garden.ephemeral.rocket.Color.Companion.grey
import garden.ephemeral.rocket.Color.Companion.white
import garden.ephemeral.rocket.Transforms.Companion.rotationX
import garden.ephemeral.rocket.Transforms.Companion.rotationY
import garden.ephemeral.rocket.Transforms.Companion.rotationZ
import garden.ephemeral.rocket.Transforms.Companion.scaling
import garden.ephemeral.rocket.Transforms.Companion.translation
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.patterns.CheckersPattern
import garden.ephemeral.rocket.shapes.Cylinder
import garden.ephemeral.rocket.shapes.Group
import garden.ephemeral.rocket.shapes.Plane
import garden.ephemeral.rocket.shapes.Sphere
import java.io.File
import kotlin.math.PI

fun main() {
    val floor = Plane().apply {
        material = Material.build {
            pattern = CheckersPattern(white, black).apply {
                transform = scaling(0.25, 0.25, 0.25)
            }
            specular = black
            reflective = grey(0.1)
        }
    }

    val leftWall = Plane().apply {
        transform = translation(0.0, 0.0, 5.0) * rotationX(PI / 2)
        material = Material.build {
            specular = black
        }
    }

    val rightWall = Plane().apply {
        transform = translation(5.0, 0.0, 0.0) * rotationZ(PI / 2)
        material = Material.build {
            specular = black
        }
    }

    val middle = Group().apply {
        transform = translation(0.0, 1.0, 0.5) * rotationX(-PI / 4)
        (0..5).forEach { n ->
            val side = Group().apply {
                transform = rotationY(n * PI / 3)
                addChild(Sphere().apply {
                    transform = translation(0.0, 0.0, -1.0) *
                                scaling(0.25, 0.25, 0.25)
                })
                addChild(Cylinder().apply {
                    minimum = 0.0
                    maximum = 1.0
                    transform = translation(0.0, 0.0, -1.0) *
                                rotationY(-PI / 6) *
                                rotationZ(-PI / 2) *
                                scaling(0.25, 1.0, 0.25)
                })
            }
            addChild(side)
        }
    }

    val right = Sphere().apply {
        transform = translation(1.0, 0.5, -1.0) * scaling(0.5, 0.5, 0.5)
        material = Material.build {
            ambient = black
            diffuse = black
            reflective = grey(0.1)
            transparency = grey(1.0)
            refractiveIndex = 1.5
        }
    }

    val left = Sphere().apply {
        transform = translation(-1.0, 0.5, -1.0) * scaling(0.5, 0.5, 0.5)
        material = Material.build {
            reflective = grey(1.0)
        }
    }

    val world = World().apply {
        objects = mutableListOf(floor, leftWall, rightWall, middle, right, left)
        lights = mutableListOf(
            PointLight(point(-12.0, 10.0, -10.0), Color(1.0, 0.0, 1.0)),
            PointLight(point(-8.0, 10.0, -10.0), Color(0.0, 1.0, 1.0))
        )
    }

    val camera = Camera(500, 250, PI / 3).apply {
        transform = viewTransform(point(0.0, 1.0, -5.0), point(0.0, 0.5, 0.0), vector(0.0, 1.0, 0.0))
    }

    val canvas = camera.render(world)

    File("out.ppm").writeText(canvas.toPPM())
}