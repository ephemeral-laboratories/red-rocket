package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Tuple

class Triangle(
    point1: Tuple,
    point2: Tuple,
    point3: Tuple,
    texturePoint1: Tuple? = null,
    texturePoint2: Tuple? = null,
    texturePoint3: Tuple? = null,
) : BaseTriangle(point1, point2, point3, texturePoint1, texturePoint2, texturePoint3) {

    val normal = edge2.cross(edge1).normalize()

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return normal
    }

    override fun toStringName(): String {
        return "Triangle"
    }

    override fun toStringParams(): String {
        return "${super.toStringParams()}, normal=$normal"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Triangle) return false
        if (!super.equals(other)) return false

        return true
    }
}
