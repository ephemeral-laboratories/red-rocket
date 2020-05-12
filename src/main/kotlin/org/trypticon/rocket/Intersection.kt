package org.trypticon.rocket

class Intersection(val t: Double, val obj: Shape) {
    companion object {
        fun hit(intersections: List<Intersection>): Intersection? {
            return intersections
                .filter { intersection -> intersection.t > 0 }
                .minBy { intersection -> intersection.t }
        }
    }

    fun prepareComputations(ray: Ray): Precomputed {
        val point = ray.position(t)
        val eyeVector = -ray.direction
        var normal = obj.worldNormalAt(point)
        val inside = normal.dot(eyeVector) < 0
        if (inside) {
            normal = -normal
        }
        return Precomputed(t, obj, point, eyeVector, normal, inside)
    }

    data class Precomputed(
        val t: Double,
        val obj: Shape,
        val point: Tuple,
        val eyeVector: Tuple,
        val normal: Tuple,
        val inside: Boolean)
}