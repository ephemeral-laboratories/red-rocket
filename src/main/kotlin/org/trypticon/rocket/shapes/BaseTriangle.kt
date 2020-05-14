package org.trypticon.rocket.shapes

import org.trypticon.rocket.Constants
import org.trypticon.rocket.Intersection
import org.trypticon.rocket.Ray
import org.trypticon.rocket.Tuple
import kotlin.math.abs

abstract class BaseTriangle(val p1: Tuple, val p2: Tuple, val p3: Tuple): Shape() {
    val e1: Tuple = p2 - p1
    val e2: Tuple = p3 - p1

    override fun localIntersect(localRay: Ray): List<Intersection> {
        val dirCrossE2 = localRay.direction.cross(e2)
        val det = e1.dot(dirCrossE2)
        if (abs(det) < Constants.epsilon) {
            return listOf()
        }

        val f = 1.0 / det
        val p1ToOrigin = localRay.origin - p1
        val u = f * p1ToOrigin.dot(dirCrossE2)
        if (u < 0 || u > 1) {
            return listOf()
        }

        val originCrossE1 = p1ToOrigin.cross(e1)
        val v = f * localRay.direction.dot(originCrossE1)
        if (v < 0 || (u + v) > 1) {
            return listOf()
        }

        val t = f * e2.dot(originCrossE1)
        return listOf(Intersection(t, this, u, v))
    }

    override fun toStringParams(): String {
        return "${super.toStringParams()}, p1=$p1, p2=$p2, p3=$p3, e1=$e1, e2=$e2"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseTriangle) return false
        if (!super.equals(other)) return false

        if (p1 != other.p1) return false
        if (p2 != other.p2) return false
        if (p3 != other.p3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + p1.hashCode()
        result = 31 * result + p2.hashCode()
        result = 31 * result + p3.hashCode()
        return result
    }
}