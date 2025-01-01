package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Intersections
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.merge

class Group : Shape() {
    val children = mutableListOf<Shape>()

    override fun localIntersect(localRay: Ray): Intersections {
        return merge(
            children
                .asSequence()
                .map { child -> child.intersect(localRay) }
        )
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        throw IllegalStateException("should not be asking a group for a normal!")
    }

    fun addChild(child: Shape) {
        children.add(child)
        child.parent = this
    }

    override fun includes(shape: Shape): Boolean {
        return children.any { child -> child.includes(shape) }
    }
}
