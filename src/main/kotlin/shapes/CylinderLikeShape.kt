package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Ray
import kotlin.math.abs

abstract class CylinderLikeShape: Shape() {
    var minimum: Double = Double.NEGATIVE_INFINITY
    var maximum: Double = Double.POSITIVE_INFINITY
    var closed: Boolean = false

    final override fun localIntersect(localRay: Ray): List<Intersection> {
        val xs: MutableList<Intersection> = mutableListOf()
        intersectWall(localRay, xs)
        intersectCaps(localRay, xs)
        return xs.toList()
    }

    protected abstract fun intersectWall(localRay: Ray, xs: MutableList<Intersection>)

    // checks to see if the intersection at `t` is within a radius
    // of 1 (the radius of your cylinders) from the y axis.
    protected abstract fun checkCap(localRay: Ray, t: Double): Boolean

    private fun intersectCaps(localRay: Ray, xs: MutableList<Intersection>) {
        // caps only matter if the cylinder is closed, and might possibly be
        // intersected by the ray.
        if (!closed || abs(localRay.direction.y) < Constants.epsilon) {
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

    override fun toStringParams(): String {
        return "${super.toStringParams()}, minimum=$minimum, maximum=$maximum, closed=$closed"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CylinderLikeShape) return false
        if (!super.equals(other)) return false

        if (minimum != other.minimum) return false
        if (maximum != other.maximum) return false
        if (closed != other.closed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + minimum.hashCode()
        result = 31 * result + maximum.hashCode()
        result = 31 * result + closed.hashCode()
        return result
    }
}