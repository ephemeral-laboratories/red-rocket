package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.util.ToStringBuilder

class SmoothTriangle(
    point1: Tuple,
    point2: Tuple,
    point3: Tuple,
    texturePoint1: Tuple?,
    texturePoint2: Tuple?,
    texturePoint3: Tuple?,
    val normal1: Tuple,
    val normal2: Tuple,
    val normal3: Tuple
) : BaseTriangle(point1, point2, point3, texturePoint1, texturePoint2, texturePoint3) {

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        return normal2 * hit.u + normal3 * hit.v + normal1 * (1 - hit.u - hit.v)
    }

    override fun toStringImpl(builder: ToStringBuilder) {
        super.toStringImpl(builder)
        builder.add(::normal1)
            .add(::normal2)
            .add(::normal3)
    }
}
