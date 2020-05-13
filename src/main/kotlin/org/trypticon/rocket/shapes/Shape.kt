package org.trypticon.rocket.shapes

import org.trypticon.rocket.*
import org.trypticon.rocket.Tuple.Companion.vector

abstract class Shape {
    var transform: Matrix = Matrix.identity4x4
    var material: Material = Material()

    fun intersect(worldRay: Ray): List<Intersection> {
        val localRay = worldRay.transform(transform.inverse())
        return localIntersect(localRay)
    }

    abstract fun localIntersect(localRay: Ray): List<Intersection>

    fun worldNormalAt(worldPoint: Tuple): Tuple {
        val localPoint = transform.inverse() * worldPoint
        val localNormal = localNormalAt(localPoint)
        val worldNormal = transform.inverse().transpose() * localNormal
        return vector(worldNormal.x, worldNormal.y, worldNormal.z).normalize()
    }

    abstract fun localNormalAt(localPoint: Tuple): Tuple

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