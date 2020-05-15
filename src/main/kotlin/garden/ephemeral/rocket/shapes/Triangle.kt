package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Tuple

class Triangle(p1: Tuple, p2: Tuple, p3: Tuple) : BaseTriangle(p1, p2, p3) {
    val normal = e2.cross(e1).normalize()

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
