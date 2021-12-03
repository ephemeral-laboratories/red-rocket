package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.point
import kotlin.math.sqrt

class Sphere : Shape() {

    override fun localIntersect(localRay: Ray): Intersections {
        val sphereToRay = localRay.origin - point(0.0, 0.0, 0.0)
        val a = localRay.direction.dot(localRay.direction)
        val b = 2 * localRay.direction.dot(sphereToRay)
        val c = sphereToRay.dot(sphereToRay) - 1
        val discriminant = b * b - 4 * a * c

        if (discriminant < 0) {
            return Intersections.None
        }

        val t1 = (-b - sqrt(discriminant)) / (2 * a)
        val t2 = (-b + sqrt(discriminant)) / (2 * a)
        return Intersections(
            Intersection(t1, this),
            Intersection(t2, this)
        )
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return localPoint - point(0.0, 0.0, 0.0)
    }
}
