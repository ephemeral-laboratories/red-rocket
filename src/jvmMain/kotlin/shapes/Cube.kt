package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.vector
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.abs

private data class AxisCheckResult(val tMin: Double, val tMax: Double)

class Cube : Shape() {
    override fun localIntersect(localRay: Ray): Intersections {
        val (xtMin, xtMax) = checkAxis(localRay.origin.x, localRay.direction.x)
        val (ytMin, ytMax) = checkAxis(localRay.origin.y, localRay.direction.y)
        val (ztMin, ztMax) = checkAxis(localRay.origin.z, localRay.direction.z)
        val tMin = maxOf(xtMin, ytMin, ztMin)
        val tMax = minOf(xtMax, ytMax, ztMax)
        if (tMin > tMax) {
            return Intersections.None
        }
        return Intersections.build {
            add(Intersection(tMin, this@Cube))
            add(Intersection(tMax, this@Cube))
        }
    }

    private fun checkAxis(origin: Double, direction: Double): AxisCheckResult {
        val tMinNumerator = (-1 - origin)
        val tMaxNumerator = (1 - origin)
        return if (abs(direction) >= epsilon) {
            val temp = AxisCheckResult(tMinNumerator / direction, tMaxNumerator / direction)
            if (temp.tMin > temp.tMax) {
                AxisCheckResult(temp.tMax, temp.tMin)
            } else {
                temp
            }
        } else {
            AxisCheckResult(tMinNumerator * POSITIVE_INFINITY, tMaxNumerator * POSITIVE_INFINITY)
        }
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return when (maxOf(abs(localPoint.x), abs(localPoint.y), abs(localPoint.z))) {
            abs(localPoint.x) -> { vector(localPoint.x, 0.0, 0.0) }
            abs(localPoint.y) -> { vector(0.0, localPoint.y, 0.0) }
            else -> { vector(0.0, 0.0, localPoint.z) }
        }
    }
}
