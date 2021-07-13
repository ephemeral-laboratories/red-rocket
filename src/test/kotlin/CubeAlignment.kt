package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.Transforms.Companion.rotationX
import garden.ephemeral.rocket.Transforms.Companion.rotationY
import garden.ephemeral.rocket.Transforms.Companion.translation
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.patterns.CubeMap
import garden.ephemeral.rocket.patterns.UVAlignCheck
import garden.ephemeral.rocket.shapes.Cube
import garden.ephemeral.rocket.shapes.Shape
import java.io.File
import java.time.Duration
import java.time.Instant

fun mappedCube(): Shape {
    return Cube().apply {
        material = Material.build {
            pattern = CubeMap(
                UVAlignCheck(Color(1.0, 1.0, 0.0), Color(0.0, 1.0, 1.0), Color(1.0, 0.0, 0.0), Color(0.0, 0.0, 1.0), Color(1.0, 0.5, 0.0)),
                UVAlignCheck(Color(0.0, 1.0, 1.0), Color(1.0, 0.0, 0.0), Color(1.0, 1.0, 0.0), Color(1.0, 0.5, 0.0), Color(0.0, 1.0, 0.0)),
                UVAlignCheck(Color(1.0, 0.0, 0.0), Color(1.0, 1.0, 0.0), Color(1.0, 0.0, 1.0), Color(0.0, 1.0, 0.0), Color(1.0, 1.0, 1.0)),
                UVAlignCheck(Color(0.0, 1.0, 0.0), Color(1.0, 0.0, 1.0), Color(0.0, 1.0, 1.0), Color(1.0, 1.0, 1.0), Color(0.0, 0.0, 1.0)),
                UVAlignCheck(Color(1.0, 0.5, 0.0), Color(0.0, 1.0, 1.0), Color(1.0, 0.0, 1.0), Color(1.0, 0.0, 0.0), Color(1.0, 1.0, 0.0)),
                UVAlignCheck(Color(1.0, 0.0, 1.0), Color(1.0, 0.5, 0.0), Color(0.0, 1.0, 0.0), Color(0.0, 0.0, 1.0), Color(1.0, 1.0, 1.0)))
            ambient = grey(0.2)
            specular = black
            diffuse = grey(0.8)
        }
    }
}

fun main() {

    val camera = Camera(800, 400, 0.8).apply {
        transform = viewTransform(point(0.0, 0.0, -20.0), point(0.0, 0.0, 0.0), vector(0.0, 1.0, 0.0))
    }

    val world = World().apply {

        lights = mutableListOf(
            PointLight(point(0.0, 100.0, -100.0), Color(0.25, 0.25, 0.25)),
            PointLight(point(0.0, -100.0, -100.0), Color(0.25, 0.25, 0.25)),
            PointLight(point(-100.0, 0.0, -100.0), Color(0.25, 0.25, 0.25)),
            PointLight(point(100.0, 0.0, -100.0), Color(0.25, 0.25, 0.25))
        )

        objects = mutableListOf(
            mappedCube().apply {
                transform = translation(-6.0, 2.0, 0.0) * rotationX(0.7854) * rotationY(0.7854)
            },
            mappedCube().apply {
                transform = translation(-2.0, 2.0, 0.0) * rotationX(0.7854) * rotationY(2.3562)
            },
            mappedCube().apply {
                transform = translation(2.0, 2.0, 0.0) * rotationX(0.7854) * rotationY(3.927)
            },
            mappedCube().apply {
                transform = translation(6.0, 2.0, 0.0) * rotationX(0.7854) * rotationY(5.4978)
            },
            mappedCube().apply {
                transform = translation(-6.0, -2.0, 0.0) * rotationX(-0.7854) * rotationY(0.7854)
            },
            mappedCube().apply {
                transform = translation(-2.0, -2.0, 0.0) * rotationX(-0.7854) * rotationY(2.3562)
            },
            mappedCube().apply {
                transform = translation(2.0, -2.0, 0.0) * rotationX(-0.7854) * rotationY(3.927)
            },
            mappedCube().apply {
                transform = translation(6.0, -2.0, 0.0) * rotationX(-0.7854) * rotationY(5.4978)
            }
        )
    }

    val t0 = Instant.now()
    val canvas = camera.render(world)
    val t1 = Instant.now()
    println("Render time: " + Duration.between(t0, t1))

    File("out.ppm").writeText(canvas.toPPM())

}

