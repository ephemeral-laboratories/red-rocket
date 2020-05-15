
import garden.ephemeral.rocket.*
import garden.ephemeral.rocket.Transforms.Companion.rotationX
import garden.ephemeral.rocket.Transforms.Companion.rotationZ
import garden.ephemeral.rocket.Transforms.Companion.scaling
import garden.ephemeral.rocket.Transforms.Companion.translation
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.Tuple.Companion.black
import garden.ephemeral.rocket.Tuple.Companion.color
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.Tuple.Companion.white
import garden.ephemeral.rocket.patterns.CheckersPattern
import garden.ephemeral.rocket.shapes.Plane
import java.io.File
import java.time.Duration
import java.time.Instant
import kotlin.math.PI

fun main() {
    val floor = Plane().apply {
        material = Material.build {
            pattern = CheckersPattern(white, black).apply {
                transform = scaling(0.25, 0.25, 0.25)
            }
            specular = 0.0
            reflective = 0.1
        }
    }

    val leftWall = Plane().apply {
        transform = translation(0.0, 0.0, 5.0) * rotationX(PI / 2)
        material = Material.build {
            specular = 0.0
        }
    }

    val rightWall = Plane().apply {
        transform = translation(5.0, 0.0, 0.0) * rotationZ(PI / 2)
        material = Material.build {
            specular = 0.0
        }
    }

    val teapot = ObjFileParser(File("src/files/teapot.obj")).objToGroup().apply {
        transform = scaling(0.125, 0.125, 0.125) * rotationX(-PI / 2)
    }

    val world = World().apply {
        objects = mutableListOf(floor, leftWall, rightWall, teapot)
        lights = mutableListOf(
            PointLight(point(-12.0, 10.0, -10.0), color(1.0, 0.0, 1.0)),
            PointLight(point(-8.0, 10.0, -10.0), color(0.0, 1.0, 1.0))
        )
    }

    val camera = Camera(1280, 720, PI / 3).apply {
        transform = viewTransform(point(0.0, 1.0, -5.0), point(0.0, 0.75, 0.0), vector(0.0, 1.0, 0.0))
    }

    val t0 = Instant.now()
    val canvas = camera.render(world)
    val t1 = Instant.now()
    println("Render time: " + Duration.between(t0, t1))

    File("out.ppm").writeText(canvas.toPPM())
}