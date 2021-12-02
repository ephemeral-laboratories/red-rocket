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

        val containers = mutableListOf<Shape>()
        var n1 = 1.0
        var n2 = 1.0
        for (i in allIntersections) {
            if (i == this) {
                val lastContainer = containers.lastOrNull()
                if (lastContainer != null) {
                    n1 = lastContainer.material.refractiveIndex
                }
            }
            if (!containers.removeByIdentity(i.obj)) {
                containers.add(i.obj)
            }
            if (i == this) {
                val lastContainer = containers.lastOrNull()
                if (lastContainer != null) {
                    n2 = lastContainer.material.refractiveIndex
                    break
                }
            }
        }

        return Precomputed(t, obj, point, overPoint, underPoint, eyeline, normal, reflection, inside, n1, n2)
    }

    // faster removal routine because we only care about object identity, not deep equality
    private fun <E> MutableList<E>.removeByIdentity(value: E): Boolean {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val child = iterator.next()
            if (child === value) {
                iterator.remove()
                return true
            }
        }
        return false
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
        val cosThetaI: Double by lazy {
            eyeline.dot(normal)
        }

        val sin2ThetaT: Double by lazy {
            val n = n1 / n2
            n.pow(2) * (1.0 - cosThetaI.pow(2))
        }

        val cosThetaT: Double by lazy {
            sqrt(1.0 - sin2ThetaT)
        }

        val tangent: Tuple by lazy {
            normal.cross(-eyeline).normalize()
        }

        val bitangent: Tuple by lazy {
            tangent.cross(normal).normalize()
        }

        fun schlick(): Double {
            var cos = cosThetaI

            // Total internal reflection can only occur if n1 > n2
            if (n1 > n2) {
                if (sin2ThetaT > 1.0) {
                    return 1.0
                }

                // when n1 > n2, use cos(theta_t) instead
                cos = cosThetaT
            }

            val r0 = ((n1 - n2) / (n1 + n2)).pow(2)
            return r0 + (1 - r0) * (1 - cos).pow(5)
        }
    }
}
