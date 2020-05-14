package org.trypticon.rocket.shapes

import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.Intersection
import org.trypticon.rocket.Ray
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.Tuple.Companion.vector
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.abs
import kotlin.math.sqrt

class Cylinder: Shape() {
    var minimum: Double = NEGATIVE_INFINITY
    var maximum: Double = POSITIVE_INFINITY
    var closed: Boolean = false

    override fun localIntersect(localRay: Ray): List<Intersection> {
        val xs: MutableList<Intersection> = mutableListOf()
        intersectWall(localRay, xs)
        intersectCaps(localRay, xs)
        return xs.toList()
    }

    private fun intersectWall(localRay: Ray, xs: MutableList<Intersection>) {
        val a = localRay.direction.x * localRay.direction.x + localRay.direction.z * localRay.direction.z
        if (abs(a) < epsilon) {
            // ray is parallel to the y axis
            return
        }

        val b = 2 * localRay.origin.x * localRay.direction.x + 2 * localRay.origin.z * localRay.direction.z
        val c = localRay.origin.x * localRay.origin.x + localRay.origin.z * localRay.origin.z - 1
        val disc = b * b - 4 * a * c
        if (disc < 0) {
            // ray does not intersect the cylinder
            return
        }

        var t0 = (-b - sqrt(disc)) / (2 * a)
        var t1 = (-b + sqrt(disc)) / (2 * a)
        if (t0 > t1) {
            val temp = t0
            t0 = t1
            t1 = temp
        }

        val y0 = localRay.origin.y + t0 * localRay.direction.y
        if (minimum < y0 && y0 < maximum) {
            xs.add(Intersection(t0, this))
        }

        val y1 = localRay.origin.y + t1 * localRay.direction.y
        if (minimum < y1 && y1 < maximum) {
            xs.add(Intersection(t1, this))
        }
    }

    // checks to see if the intersection at `t` is within a radius
    // of 1 (the radius of your cylinders) from the y axis.
    private fun checkCap(localRay: Ray, t: Double): Boolean {
        val x = localRay.origin.x + t * localRay.direction.x
        val z = localRay.origin.z + t * localRay.direction.z
        return (x * x + z * z) <= 1
    }

    private fun intersectCaps(localRay: Ray, xs: MutableList<Intersection>) {
        // caps only matter if the cylinder is closed, and might possibly be
        // intersected by the ray.
        if (!closed || abs(localRay.direction.y) < epsilon) {
            return
        }

        // check for an intersection with the lower end cap by intersecting
        // the ray with the plane at y = cyl.minimum
        val t0 = (minimum - localRay.origin.y) / localRay.direction.y
        if (checkCap(localRay, t0)) {
            xs.add(Intersection(t0, this))
        }

        // check for an intersection with the upper end cap by intersecting
        // the ray with the plane at y = cyl.maximum
        val t1 = (maximum - localRay.origin.y) / localRay.direction.y
        if (checkCap(localRay, t1)) {
            xs.add(Intersection(t1, this))
        }
    }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z
        return if (dist < 1 && localPoint.y >= maximum - epsilon) {
            vector(0.0, 1.0, 0.0)
        } else if (dist < 1 && localPoint.y <= minimum + epsilon) {
            vector(0.0, -1.0, 0.0)
        } else {
            vector(localPoint.x, 0.0, localPoint.z)
        }
    }

    override fun toStringName(): String {
        return "Cylinder"
    }
}