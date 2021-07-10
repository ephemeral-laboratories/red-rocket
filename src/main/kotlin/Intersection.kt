package garden.ephemeral.rocket

import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.shapes.Shape
import kotlin.math.pow
import kotlin.math.sqrt

data class Intersection(val t: Double, val obj: Shape, val u: Double = 0.0, val v: Double = 0.0) {
    companion object {
        fun hit(intersections: List<Intersection>): Intersection? {
            return intersections
                .filter { intersection -> intersection.t > 0 }
                .minByOrNull { intersection -> intersection.t }
        }
    }

    fun prepareComputations(ray: Ray, allIntersections: List<Intersection>): Precomputed {
        val point = ray.position(t)
        val eyeline = -ray.direction
        var normal = obj.worldNormalAt(point, this)
        val inside = normal.dot(eyeline) < 0
        if (inside) {
            normal = -normal
        }
        val reflection = ray.direction.reflect(normal)
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

        return Precomputed(t, obj, point, overPoint, underPoint, eyeline, normal, reflection, inside, n1, n2)
    }

    data class Precomputed(
        val t: Double,
        val obj: Shape,
        val point: Tuple,
        val overPoint: Tuple,
        val underPoint: Tuple,
        val eyeline: Tuple,
        val normal: Tuple,
        val reflection: Tuple,
        val inside: Boolean,
        val n1: Double,
        val n2: Double
    ) {
        fun schlick(): Double {
            var cos = eyeline.dot(normal)

            // Total internal reflection can only occur if n1 > n2
            if (n1 > n2) {
                val n = n1 / n2
                val sin2t = n * n * (1.0 - cos * cos)
                if (sin2t > 1.0) {
                    return 1.0
                }

                // compute cosine of theta_t using trig identity
                val cosT = sqrt(1.0 - sin2t)

                // when n1 > n2, use cos(theta_t) instead
                cos = cosT
            }

            var r0 = ((n1 - n2) / (n1 + n2))
            r0 *= r0
            return r0 + (1 - r0) * (1 - cos).pow(5)
        }
    }
}