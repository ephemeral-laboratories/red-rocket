package org.trypticon.rocket.shapes

import org.trypticon.rocket.*
import org.trypticon.rocket.Tuple.Companion.vector

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

    fun intersect(worldRay: Ray): List<Intersection> {
        val localRay = worldRay.transform(transform.inverse)
        return localIntersect(localRay)
    }

    abstract fun localIntersect(localRay: Ray): List<Intersection>

    fun worldNormalAt(worldPoint: Tuple, intersection: Intersection): Tuple {
        val localPoint = worldToObject(worldPoint)
        val localNormal = localNormalAt(localPoint, intersection)
        return normalToWorld(localNormal)
    }

    abstract fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple

    final override fun toString(): String {
        return "${toStringName()}(${toStringParams()})"
    }

    abstract fun toStringName(): String

    open fun toStringParams(): String {
        return "transform=$transform, material=$material"
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
}