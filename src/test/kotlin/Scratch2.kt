
import org.trypticon.rocket.*
import org.trypticon.rocket.Transforms.Companion.rotationX
import org.trypticon.rocket.Transforms.Companion.rotationZ
import org.trypticon.rocket.Transforms.Companion.scaling
import org.trypticon.rocket.Transforms.Companion.translation
import org.trypticon.rocket.Transforms.Companion.viewTransform
import org.trypticon.rocket.Tuple.Companion.black
import org.trypticon.rocket.Tuple.Companion.color
import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.vector
import org.trypticon.rocket.Tuple.Companion.white
import org.trypticon.rocket.patterns.CheckersPattern
import org.trypticon.rocket.shapes.Plane
import java.io.File
import java.time.Duration
import java.time.Instant
import kotlin.math.PI

fun main() {
    val floor = Plane().apply {
        material = Material().apply {
            pattern = CheckersPattern(white, black).apply {
                transform = scaling(0.25, 0.25, 0.25)
            }
            specular = 0.0
            reflective = 0.1
        }
    }

    val leftWall = Plane().apply {
        transform = translation(0.0, 0.0, 5.0) * rotationX(PI / 2)
        material = Material().apply {
            specular = 0.0
        }
    }

    val rightWall = Plane().apply {
        transform = translation(5.0, 0.0, 0.0) * rotationZ(PI / 2)
        material = Material().apply {
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
            PointLight(point(-8.0, 10.0, -10.0), color(0.0, 1.0, 1.0)))
    }

    val camera = Camera(1280/8, 720/8, PI / 3).apply {
//    val camera = Camera(1280, 720, PI / 3).apply {
        transform = viewTransform(point(0.0, 1.0, -5.0), point(0.0, 0.75, 0.0), vector(0.0, 1.0, 0.0))
    }

    val t0 = Instant.now()
    val canvas = camera.render(world)
    val t1 = Instant.now()
    System.out.println("Render time: " + Duration.between(t0, t1))

    File("out.ppm").writeText(canvas.toPPM())
}