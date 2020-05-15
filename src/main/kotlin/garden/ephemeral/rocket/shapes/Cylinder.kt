package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.vector
import kotlin.math.abs
import kotlin.math.sqrt

class Cylinder: CylinderLikeShape() {
    override fun intersectWall(localRay: Ray, xs: MutableList<Intersection>) {
        val o = localRay.origin
        val d = localRay.direction
        val a = d.x * d.x + d.z * d.z
        if (abs(a) < epsilon) {
            // ray is parallel to the y axis
            return
        }

        val b = 2 * o.x * d.x + 2 * o.z * d.z
        val c = o.x * o.x + o.z * o.z - 1
        val disc = b * b - 4 * a * c
        if (disc < 0) {
            // ray does not intersect the wall
            return
        }

        var t0 = (-b - sqrt(disc)) / (2 * a)
        var t1 = (-b + sqrt(disc)) / (2 * a)
        if (t0 > t1) {
            val temp = t0
            t0 = t1
            t1 = temp
        }

        val y0 = o.y + t0 * d.y
        if (minimum < y0 && y0 < maximum) {
            xs.add(Intersection(t0, this))
        }

        val y1 = o.y + t1 * d.y
        if (minimum < y1 && y1 < maximum) {
            xs.add(Intersection(t1, this))
        }
    }

    override fun checkCap(localRay: Ray, t: Double): Boolean {
        val x = localRay.origin.x + t * localRay.direction.x
        val z = localRay.origin.z + t * localRay.direction.z
        return (x * x + z * z) <= 1
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cylinder) return false
        if (!super.equals(other)) return false
        return true
    }
}