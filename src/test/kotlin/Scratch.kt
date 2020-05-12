import org.trypticon.rocket.*
import org.trypticon.rocket.Intersection.Companion.hit
import org.trypticon.rocket.Lighting.Companion.lighting
import java.io.File

fun main() {
    val lightPosition = Tuple.point(-10.0, 10.0, -10.0)
    val lightColor = Tuple.color(1.0, 1.0, 1.0)
    val light = PointLight(lightPosition, lightColor)

    val canvasPixels = 512
    val canvas = Canvas(canvasPixels, canvasPixels)

    val shape = Sphere()
    shape.material = Material()
    shape.material.color = Tuple.color(1.0, 0.2, 1.0)

    val wallSize = 7.0
    val wallZ = 10.0
    val pixelSize = wallSize / canvasPixels
    val half = wallSize / 2

    val rayOrigin = Tuple.point(0.0, 0.0, -5.0)

    (0 until canvasPixels).forEach { y: Int ->
        // compute the world y coordinate (top = +half, bottom = -half)
        val worldY = half - pixelSize * y
        (0 until canvasPixels).forEach { x: Int ->
            // compute the world x coordinate(left = -half, right = half)
            val worldX = -half + pixelSize * x
            // describe the point on the wall that the ray will target
            val position = Tuple.point(worldX, worldY, wallZ)
            val ray = Ray(rayOrigin, (position - rayOrigin).normalize())
            val xs = shape.intersect(ray)
            val hit = hit(xs)
            if (hit != null) {
                val point = ray.position(hit.t)
                val normal = hit.obj.worldNormalAt(point)
                val eye = -ray.direction
                val color = lighting(hit.obj.material, light, point, eye, normal)
                canvas.setPixel(x, y, color)
            }
        }
    }

    File("out.ppm").writeText(canvas.toPPM())
}