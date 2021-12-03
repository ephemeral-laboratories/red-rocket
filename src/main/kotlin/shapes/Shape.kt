package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.*
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.util.ToStringBuilder

abstract class Shape {
    var parent: Shape? = null
    var transform: Matrix = Matrix.identity4x4
    var material: Material = Material.default

    fun worldToObject(worldPoint: Tuple): Tuple {
        val parentObjectPoint = parent?.worldToObject(worldPoint) ?: worldPoint
        return transform.inverse * parentObjectPoint
    }

    fun normalToWorld(objectNormal: Tuple): Tuple {
        var normal = transform.inverse.transpose() * objectNormal
        normal = vector(normal.x, normal.y, normal.z).normalize()
        normal = parent?.normalToWorld(normal) ?: normal
        return normal
    }

    fun intersect(worldRay: Ray): Intersections {
        val localRay = worldRay.transform(transform.inverse)
        return localIntersect(localRay)
    }

    abstract fun localIntersect(localRay: Ray): Intersections

    fun worldNormalAt(worldPoint: Tuple, intersection: Intersection): Tuple {
        val localPoint = worldToObject(worldPoint)
        val localNormal = localNormalAt(localPoint, intersection)
        return normalToWorld(localNormal)
    }

    abstract fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple

    open fun includes(shape: Shape): Boolean {
        return shape === this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shape) return false

        if (transform != other.transform) return false
        if (material != other.material) return false

        return true
    }

    override fun hashCode(): Int {
        var result = transform.hashCode()
        result = 31 * result + material.hashCode()
        return result
    }

    final override fun toString(): String = ToStringBuilder(this).apply(::toStringImpl).toString()

    open fun toStringImpl(builder: ToStringBuilder) {
        builder
            .add(::transform)
            .add(::material)
    }
}
