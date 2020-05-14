package org.trypticon.rocket.shapes

import org.trypticon.rocket.Intersection
import org.trypticon.rocket.Tuple

class SmoothTriangle(p1: Tuple, p2: Tuple, p3: Tuple, val n1: Tuple, val n2: Tuple, val n3: Tuple):
    BaseTriangle(p1, p2, p3) {

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return n2 * hit.u + n3 * hit.v + n1 * (1 - hit.u - hit.v)
    }

    override fun toStringName(): String {
        return "SmoothTriangle"
    }

    override fun toStringParams(): String {
        return "${super.toStringParams()}, n1=$n1, n2=$n2, n3=$n3"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SmoothTriangle) return false
        if (!super.equals(other)) return false

        if (n1 != other.n1) return false
        if (n2 != other.n2) return false
        if (n3 != other.n3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + n1.hashCode()
        result = 31 * result + n2.hashCode()
        result = 31 * result + n3.hashCode()
        return result
    }
}
