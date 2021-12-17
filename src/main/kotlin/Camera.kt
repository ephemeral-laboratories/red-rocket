package garden.ephemeral.rocket

import garden.ephemeral.rocket.Matrix.Companion.identity4x4
import garden.ephemeral.rocket.Tuple.Companion.origin
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.util.Angle
import garden.ephemeral.rocket.util.tan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking

data class Camera(val hSize: Int, val vSize: Int, val fieldOfView: Angle) {
    private val halfWidth: Double
    private val halfHeight: Double
    val pixelSize: Double
    var transform: Matrix = identity4x4

    init {
        val halfView = tan(fieldOfView / 2)
        val aspect = hSize / vSize.toDouble()
        if (aspect >= 1) {
            halfWidth = halfView
            halfHeight = halfView / aspect
        } else {
            halfWidth = halfView * aspect
            halfHeight = halfView
        }
        pixelSize = (halfWidth * 2) / hSize
    }

    fun rayForPixel(px: Int, py: Int): Ray {
        // Offset from the edge of the canvas to the pixel's center
        val xOffset = (px + 0.5) * pixelSize
        val yOffset = (py + 0.5) * pixelSize

        // Untransformed coordinates of the pixel in world space.
        // (remember that the camera looks toward -z, so +x is to the *left*.)
        val worldX = halfWidth - xOffset
        val worldY = halfHeight - yOffset

        // Using the camera matrix, transform the canvas point and the origin,
        // and then compute the ray's direction vector.
        // (remember that the canvas is at z=-1)
        val pixel = transform.inverse * point(worldX, worldY, -1.0)
        val origin = transform.inverse * origin
        val direction = (pixel - origin).normalize()

        return Ray(origin, direction)
    }

    fun render(world: World): Canvas {
        return Canvas(hSize, vSize).apply {
            runBlocking {
                suspend fun <E> Iterable<E>.parallelForEach(f: suspend (E) -> Unit): Unit = coroutineScope {
                    map { job -> async(Dispatchers.Default) { f(job) } }.joinAll()
                }

                (0 until vSize).parallelForEach { y ->
                    (0 until hSize).forEach { x ->
                        val ray = rayForPixel(x, y)
                        val color = world.colorAt(ray)
                        setPixel(x, y, color)
                    }
                }
            }
        }
    }
}
