package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.vector
import kotlin.math.abs

class Plane : Shape() {
    override fun localIntersect(localRay: Ray): Intersections {
        // Parallel to the plane, or coplanar => no intersections
        if (abs(localRay.direction.y) < epsilon) {
            return Intersections.None
        }

        // Intersecting from above, or from below
        val t = -localRay.origin.y / localRay.direction.y
        return Intersections(Intersection(t, this))
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return vector(0.0, 1.0, 0.0)
    }
}
