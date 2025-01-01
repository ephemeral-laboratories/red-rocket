package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.util.ToStringBuilder
import kotlin.math.abs

abstract class BaseTriangle(
    val point1: Tuple,
    val point2: Tuple,
    val point3: Tuple,
    val texturePoint1: Tuple?,
    val texturePoint2: Tuple?,
    val texturePoint3: Tuple?
) : Shape() {

    val edge1: Tuple = point2 - point1
    val edge2: Tuple = point3 - point1

    final override fun localIntersect(localRay: Ray): Intersections {
        val dirCrossE2 = localRay.direction.cross(edge2)
        val det = edge1.dot(dirCrossE2)
        if (abs(det) < epsilon) {
            return Intersections.None
        }

        val f = 1.0 / det
        val p1ToOrigin = localRay.origin - point1
        val u = f * p1ToOrigin.dot(dirCrossE2)
        if (u < 0 || u > 1) {
            return Intersections.None
        }

        val originCrossE1 = p1ToOrigin.cross(edge1)
        val v = f * localRay.direction.dot(originCrossE1)
        if (v < 0 || (u + v) > 1) {
            return Intersections.None
        }

        val t = f * edge2.dot(originCrossE1)
        return Intersections.single(Intersection(t, this, u, v))
    }

    override fun toStringImpl(builder: ToStringBuilder) {
        super.toStringImpl(builder)
        builder.add(::point1)
            .add(::point1)
            .add(::point1)
            .add(::texturePoint1)
            .add(::texturePoint2)
            .add(::texturePoint3)
            .add(::edge1)
            .add(::edge2)
    }
}
