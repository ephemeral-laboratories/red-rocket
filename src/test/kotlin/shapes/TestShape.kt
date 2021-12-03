package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.vector

class TestShape : Shape() {
    var savedRay: Ray? = null

    override fun localIntersect(localRay: Ray): Intersections {
        savedRay = localRay
        return Intersections.None
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return vector(localPoint.x, localPoint.y, localPoint.z)
    }
}
