package org.trypticon.rocket

import org.trypticon.rocket.Constants.Companion.epsilon
import org.trypticon.rocket.shapes.Shape

class Intersection(val t: Double, val obj: Shape) {
    companion object {
        fun hit(intersections: List<Intersection>): Intersection? {
            return intersections
                .filter { intersection -> intersection.t > 0 }
                .minBy { intersection -> intersection.t }
        }
    }

    fun prepareComputations(ray: Ray, allIntersections: List<Intersection>): Precomputed {
        val point = ray.position(t)
        val eyeVector = -ray.direction
        var normal = obj.worldNormalAt(point)
        val inside = normal.dot(eyeVector) < 0
        if (inside) {
            normal = -normal
        }
        val reflectVector = ray.direction.reflect(normal)
        val overPoint = point + normal * epsilon
        val underPoint = point - normal * epsilon

        val containers : MutableList<Shape> = mutableListOf()
        var n1 = 1.0
        var n2 = 1.0
        for (i in allIntersections) {
            if (i == this) {
                n1 = containers.lastOrNull()?.material?.refractiveIndex ?: 1.0
            }
            if (containers.contains(i.obj)) {
                containers.remove(i.obj)
            } else {
                containers.add(i.obj)
            }
            if (i == this) {
                n2 = containers.lastOrNull()?.material?.refractiveIndex ?: 1.0
                break
            }
        }

        return Precomputed(t, obj, point, overPoint, underPoint, eyeVector, normal, reflectVector, inside, n1, n2)
    }

    data class Precomputed(
        val t: Double,
        val obj: Shape,
        val point: Tuple,
        val overPoint: Tuple,
        val underPoint: Tuple,
        val eyeVector: Tuple,
        val normal: Tuple,
        val reflectVector: Tuple,
        val inside: Boolean,
        val n1: Double,
        val n2: Double)
}