
import org.trypticon.rocket.Camera
import org.trypticon.rocket.Material
import org.trypticon.rocket.PointLight
import org.trypticon.rocket.Transforms.Companion.rotationX
import org.trypticon.rocket.Transforms.Companion.rotationZ
import org.trypticon.rocket.Transforms.Companion.scaling
import org.trypticon.rocket.Transforms.Companion.translation
import org.trypticon.rocket.Transforms.Companion.viewTransform
import org.trypticon.rocket.Tuple.Companion.color
import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.vector
import org.trypticon.rocket.World
import org.trypticon.rocket.shapes.Plane
import org.trypticon.rocket.shapes.Sphere
import java.io.File
import kotlin.math.PI

fun main() {
    val floor = Plane().apply {
        material = Material().apply {
            color = color(1.0, 0.9, 0.9)
            specular = 0.0
        }
    }

    val leftWall = Plane().apply {
        transform = translation(0.0, 0.0, 5.0) * rotationX(PI / 2)
        material = floor.material
    }

    val rightWall = Plane().apply {
        transform = translation(5.0, 0.0, 0.0) * rotationZ(PI / 2)
        material = floor.material
    }

    val middle = Sphere().apply {
        transform = translation(-0.5, 1.0, 0.5)
        material = Material().apply {
            color = color(0.1, 1.0, 0.5)
            diffuse = 0.7
            specular = 0.3
            reflective = 0.5
        }
    }

    val right = Sphere().apply {
        transform = translation(1.5, 0.5, -0.5) * scaling(0.5, 0.5, 0.5)
        material = Material().apply {
            color = color(0.5, 1.0, 0.1)
            diffuse = 0.7
            specular = 0.3
        }
    }

    val left = Sphere().apply {
        transform = translation(-1.5, 0.33, -0.75) * scaling(0.33, 0.33, 0.33)
        material = Material().apply {
            color = color(1.0, 0.8, 0.1)
            diffuse = 0.7
            specular = 0.3
        }
    }

    val world = World().apply {
        objects = mutableListOf(floor, leftWall, rightWall, middle, right, left)
        lights = mutableListOf(
            PointLight(point(-12.0, 10.0, -10.0), color(1.0, 0.0, 1.0)),
            PointLight(point(-8.0, 10.0, -10.0), color(0.0, 1.0, 1.0)))
    }

    val camera = Camera(500, 250, PI/3).apply {
        transform = viewTransform(point(0.0, 1.5, -5.0), point(0.0, 1.0, 0.0), vector(0.0, 1.0, 0.0))
    }

    val canvas = camera.render(world)

    File("out.ppm").writeText(canvas.toPPM())
}