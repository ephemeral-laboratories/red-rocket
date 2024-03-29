package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.util.ToStringBuilder
import kotlin.math.abs

abstract class CylinderLikeShape : Shape() {
    var minimum: Double = Double.NEGATIVE_INFINITY
    var maximum: Double = Double.POSITIVE_INFINITY
    var isClosed: Boolean = false

    final override fun localIntersect(localRay: Ray): Intersections {
        return Intersections.build {
            intersectWall(localRay, this)
            intersectCaps(localRay, this)
        }
    }

    protected abstract fun intersectWall(localRay: Ray, builder: Intersections.Builder)

    // checks to see if the intersection at `t` is within a radius
    // of 1 (the radius of your cylinders) from the y-axis.
    protected abstract fun checkCap(localRay: Ray, t: Double): Boolean

    private fun intersectCaps(localRay: Ray, builder: Intersections.Builder) {
        // caps only matter if the cylinder is closed, and might be
        // intersected by the ray.
        if (!isClosed || abs(localRay.direction.y) < Constants.epsilon) {
            return
        }

        // check for an intersection with the lower end cap by intersecting
        // the ray with the plane at y = cyl.minimum
        val t0 = (minimum - localRay.origin.y) / localRay.direction.y
        if (checkCap(localRay, t0)) {
            builder.add(Intersection(t0, this))
        }

        // check for an intersection with the upper end cap by intersecting
        // the ray with the plane at y = cyl.maximum
        val t1 = (maximum - localRay.origin.y) / localRay.direction.y
        if (checkCap(localRay, t1)) {
            builder.add(Intersection(t1, this))
        }
    }

    override fun toStringImpl(builder: ToStringBuilder) {
        super.toStringImpl(builder)
        builder.add(::minimum)
            .add(::maximum)
            .add(::isClosed)
    }
}
