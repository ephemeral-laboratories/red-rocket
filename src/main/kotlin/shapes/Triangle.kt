package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.util.ToStringBuilder

class Triangle(
    point1: Tuple,
    point2: Tuple,
    point3: Tuple,
    texturePoint1: Tuple? = null,
    texturePoint2: Tuple? = null,
    texturePoint3: Tuple? = null
) : BaseTriangle(point1, point2, point3, texturePoint1, texturePoint2, texturePoint3) {

    val normal = edge2.cross(edge1).normalize()

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return normal
    }

    override fun toStringImpl(builder: ToStringBuilder) {
        super.toStringImpl(builder)
        builder.add(::normal)
    }
}
