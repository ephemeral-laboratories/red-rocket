package org.trypticon.rocket.shapes

import org.trypticon.rocket.Intersection
import org.trypticon.rocket.Ray
import org.trypticon.rocket.Tuple

class Group: Shape() {
    val children: MutableList<Shape> = mutableListOf()

    override fun localIntersect(localRay: Ray): List<Intersection> {
        return children
            .flatMap { child -> child.intersect(localRay) }
            .sortedBy { intersection -> intersection.t }
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        throw IllegalStateException("should not be asking a group for a normal!")
    }

    fun addChild(child: Shape) {
        children.add(child)
        child.parent = this
    }

    override fun toStringName(): String {
        return "Group"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Group) return false
        if (!super.equals(other)) return false

        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + children.hashCode()
        return result
    }
}