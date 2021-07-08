package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import kotlin.math.abs

abstract class BaseTriangle(val point1: Tuple, val point2: Tuple, val point3: Tuple): Shape() {
    val edge1: Tuple = point2 - point1
    val edge2: Tuple = point3 - point1

    final override fun localIntersect(localRay: Ray): List<Intersection> {
        val dirCrossE2 = localRay.direction.cross(edge2)
        val det = edge1.dot(dirCrossE2)
        if (abs(det) < epsilon) {
            return listOf()
        }

        val f = 1.0 / det
        val p1ToOrigin = localRay.origin - point1
        val u = f * p1ToOrigin.dot(dirCrossE2)
        if (u < 0 || u > 1) {
            return listOf()
        }

        val originCrossE1 = p1ToOrigin.cross(edge1)
        val v = f * localRay.direction.dot(originCrossE1)
        if (v < 0 || (u + v) > 1) {
            return listOf()
        }

        val t = f * edge2.dot(originCrossE1)
        return listOf(Intersection(t, this, u, v))
    }

    override fun toStringParams(): String {
        return "${super.toStringParams()}, p1=$point1, p2=$point2, p3=$point3, e1=$edge1, e2=$edge2"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseTriangle) return false
        if (!super.equals(other)) return false

        if (point1 != other.point1) return false
        if (point2 != other.point2) return false
        if (point3 != other.point3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + point1.hashCode()
        result = 31 * result + point2.hashCode()
        result = 31 * result + point3.hashCode()
        return result
    }
}