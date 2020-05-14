package org.trypticon.rocket.shapes

import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.Intersection
import org.trypticon.rocket.Ray
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.Tuple.Companion.vector
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.abs

class Cube: Shape() {
    override fun localIntersect(localRay: Ray): List<Intersection> {
        val (xtMin, xtMax) = checkAxis(localRay.origin.x, localRay.direction.x)
        val (ytMin, ytMax) = checkAxis(localRay.origin.y, localRay.direction.y)
        val (ztMin, ztMax) = checkAxis(localRay.origin.z, localRay.direction.z)
        val tMin = maxOf(xtMin, ytMin, ztMin)
        val tMax = minOf(xtMax, ytMax, ztMax)
        if (tMin > tMax) {
            return listOf()
        }
        return listOf(Intersection(tMin, this), Intersection(tMax, this) )
    }

    private fun checkAxis(origin: Double, direction: Double): Pair<Double, Double> {
        val tMinNumerator = (-1 - origin)
        val tMaxNumerator = (1 - origin)
        var tMinMax = if (abs(direction) >= epsilon) {
            Pair(tMinNumerator / direction, tMaxNumerator / direction)
        } else {
            Pair(tMinNumerator * POSITIVE_INFINITY, tMaxNumerator * POSITIVE_INFINITY)
        }
        if (tMinMax.first > tMinMax.second) {
            tMinMax = Pair(tMinMax.second, tMinMax.first)
        }
        return tMinMax
    }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        return when (maxOf(abs(localPoint.x), abs(localPoint.y), abs(localPoint.z))) {
            abs(localPoint.x) -> { vector(localPoint.x, 0.0, 0.0) }
            abs(localPoint.y) -> { vector(0.0, localPoint.y, 0.0) }
            else              -> { vector(0.0, 0.0, localPoint.z) }
        }
    }

    override fun toStringName(): String {
        return "Cube"
    }
}