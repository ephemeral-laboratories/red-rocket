package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.white
import garden.ephemeral.rocket.Transforms.Companion.rotationX
import garden.ephemeral.rocket.Transforms.Companion.scaling
import garden.ephemeral.rocket.Transforms.Companion.translation
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.shapes.Cube
import garden.ephemeral.rocket.shapes.Plane
import garden.ephemeral.rocket.shapes.Sphere
import java.io.File
import java.time.Duration
import java.time.Instant
import kotlin.math.PI

fun main() {
    val whiteMaterial = Material.build {
        color = white
        diffuse = grey(0.7)
        ambient = grey(0.1)
        specular = black
        reflective = grey(0.1)
    }

    val blueMaterial = whiteMaterial.build {
        color = Color(0.537, 0.831, 0.914)
    }

    val redMaterial = whiteMaterial.build {
        color = Color(0.941, 0.322, 0.388)
    }

    val purpleMaterial = whiteMaterial.build {
        color = Color(0.373, 0.404, 0.550)
    }

    val standardTransform = scaling(0.5, 0.5, 0.5) * translation(1.0, -1.0, 1.0)
    val largeObject = scaling(3.5, 3.5, 3.5) * standardTransform
    val mediumObject = scaling(3.0, 3.0, 3.0) * standardTransform
    val smallObject = scaling(2.0, 2.0, 2.0) * standardTransform

    val world = World().apply {
        lights = mutableListOf(
            PointLight(point(50.0, 100.0, -50.0), white),
            PointLight(point(-400.0, 50.0, -10.0), Color(0.2, 0.2, 0.2))
        )
        objects = mutableListOf(
            Plane().apply {
                material = Material.build {
                    color = white
                    ambient = white
                    diffuse = black
                    specular = black
                }
                transform = translation(0.0, 0.0, 500.0) * rotationX(PI / 2)
            },
            Sphere().apply {
                material = Material.build {
                    color = Color(0.373, 0.404, 0.550)
                    diffuse = grey(0.2)
                    ambient = black
                    specular = white
                    shininess = 200.0
                    reflective = grey(0.7)
                    transparency = grey(0.7)
                    refractiveIndex = 1.5
                }
                transform = largeObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(4.0, 0.0, 0.0) * mediumObject
            },
            Cube().apply {
                material = blueMaterial
                transform = translation(9.5, 1.5, -0.5) * largeObject
            },
            Cube().apply {
                material = blueMaterial
                transform = translation(8.5, 1.5, -0.5) * largeObject
            },
            Cube().apply {
                material = redMaterial
                transform = translation(0.0, 0.0, 4.0) * largeObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(4.0, 0.0, 4.0) * smallObject
            },
            Cube().apply {
                material = purpleMaterial
                transform = translation(7.5, 0.5, 4.0) * mediumObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(-0.25, 0.25, 8.0) * mediumObject
            },
            Cube().apply {
                material = blueMaterial
                transform = translation(4.0, 1.0, 7.5) * largeObject
            },
            Cube().apply {
                material = redMaterial
                transform = translation(10.0, 2.0, 7.5) * mediumObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(8.0, 2.0, 12.0) * smallObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(20.0, 1.0, 9.0) * smallObject
            },
            Cube().apply {
                material = blueMaterial
                transform = translation(-0.5, -5.0, 0.25) * largeObject
            },
            Cube().apply {
                material = redMaterial
                transform = translation(4.0, -4.0, 0.0) * largeObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(8.5, -4.0, 0.0) * largeObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(0.0, -4.0, 4.0) * largeObject
            },
            Cube().apply {
                material = purpleMaterial
                transform = translation(-0.5, -4.5, 8.0) * largeObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(0.0, -8.0, 4.0) * largeObject
            },
            Cube().apply {
                material = whiteMaterial
                transform = translation(-0.5, -8.5, 8.0) * largeObject
            }
        )
    }

    val camera = Camera(800, 800, 0.785).apply {
        transform = viewTransform(point(-6.0, 6.0, -10.0), point(6.0, 0.0, 6.0), vector(-0.45, 1.0, 0.0))
    }

    val t0 = Instant.now()
    val canvas = camera.render(world)
    val t1 = Instant.now()
    println("Render time: " + Duration.between(t0, t1))

    canvas.toPPM(File("out.ppm"))
    canvas.toPNG(File("out.png"))
}