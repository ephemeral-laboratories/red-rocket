package org.trypticon.rocket

abstract class Shape() {
    var transform: Matrix = Matrix.identity4x4
    var material: Material = Material()

    abstract fun intersect(ray: Ray): List<Intersection>

    abstract fun worldNormalAt(worldPoint: Tuple): Tuple

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