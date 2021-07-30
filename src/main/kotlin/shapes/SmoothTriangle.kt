package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Tuple

class SmoothTriangle(
    point1: Tuple, point2: Tuple, point3: Tuple,
    texturePoint1: Tuple?, texturePoint2: Tuple?, texturePoint3: Tuple?,
    val normal1: Tuple, val normal2: Tuple, val normal3: Tuple
) : BaseTriangle(point1, point2, point3, texturePoint1, texturePoint2, texturePoint3) {

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return normal2 * hit.u + normal3 * hit.v + normal1 * (1 - hit.u - hit.v)
    }

    override fun toStringName(): String {
        return "SmoothTriangle"
    }

    override fun toStringParams(): String {
        return "${super.toStringParams()}, n1=$normal1, n2=$normal2, n3=$normal3"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SmoothTriangle) return false
        if (!super.equals(other)) return false

        if (normal1 != other.normal1) return false
        if (normal2 != other.normal2) return false
        if (normal3 != other.normal3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + normal1.hashCode()
        result = 31 * result + normal2.hashCode()
        result = 31 * result + normal3.hashCode()
        return result
    }
}
