package org.trypticon.rocket.shapes

import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.Intersection
import org.trypticon.rocket.Ray
import org.trypticon.rocket.Tuple
import org.trypticon.rocket.Tuple.Companion.vector
import kotlin.math.abs

class Plane: Shape() {
    override fun localIntersect(localRay: Ray): List<Intersection> {
        // Parallel to the plane, or coplanar => no intersections
        if (abs(localRay.direction.y) < epsilon) {
            return listOf()
        }

        // Intersecting from above, or from below
        val t = -localRay.origin.y / localRay.direction.y
        return listOf(Intersection(t, this))
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return vector(0.0, 1.0, 0.0)
    }

    override fun toStringName(): String {
        return "Plane"
    }
}