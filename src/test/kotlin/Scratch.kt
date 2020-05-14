
import org.trypticon.rocket.Camera
import org.trypticon.rocket.Material
import org.trypticon.rocket.PointLight
import org.trypticon.rocket.Transforms.Companion.rotationX
import org.trypticon.rocket.Transforms.Companion.rotationY
import org.trypticon.rocket.Transforms.Companion.rotationZ
import org.trypticon.rocket.Transforms.Companion.scaling
import org.trypticon.rocket.Transforms.Companion.translation
import org.trypticon.rocket.Transforms.Companion.viewTransform
import org.trypticon.rocket.Tuple.Companion.black
import org.trypticon.rocket.Tuple.Companion.color
import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.vector
import org.trypticon.rocket.Tuple.Companion.white
import org.trypticon.rocket.World
import org.trypticon.rocket.patterns.CheckersPattern
import org.trypticon.rocket.shapes.Cylinder
import org.trypticon.rocket.shapes.Group
import org.trypticon.rocket.shapes.Plane
import org.trypticon.rocket.shapes.Sphere
import java.io.File
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
        material = Material().apply {
            ambient = 0.0
            diffuse = 0.0
            reflective = 0.1
            transparency = 1.0
            refractiveIndex = 1.5
        }
    }

    val left = Sphere().apply {
        transform = translation(-1.0, 0.5, -1.0) * scaling(0.5, 0.5, 0.5)
        material = Material().apply {
            reflective = 1.0
        }
    }

    val world = World().apply {
        objects = mutableListOf(floor, leftWall, rightWall, middle, right, left)
        lights = mutableListOf(
            PointLight(point(-12.0, 10.0, -10.0), color(1.0, 0.0, 1.0)),
            PointLight(point(-8.0, 10.0, -10.0), color(0.0, 1.0, 1.0)))
    }

    val camera = Camera(500, 250, PI/3).apply {
        transform = viewTransform(point(0.0, 1.0, -5.0), point(0.0, 0.5, 0.0), vector(0.0, 1.0, 0.0))
    }

    val canvas = camera.render(world)

    File("out.ppm").writeText(canvas.toPPM())
}