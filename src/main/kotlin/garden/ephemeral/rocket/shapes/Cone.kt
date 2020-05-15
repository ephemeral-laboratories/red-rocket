package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.vector
import kotlin.math.abs
import kotlin.math.sqrt

class Cone: CylinderLikeShape() {
    override fun intersectWall(localRay: Ray, xs: MutableList<Intersection>) {
        val o = localRay.origin
        val d = localRay.direction
        val a = d.x * d.x - d.y * d.y + d.z * d.z
        val b = 2 * o.x * d.x - 2 * o.y * d.y + 2 * o.z * d.z
        if (abs(a) < epsilon && abs(b) < epsilon) {
            return
        }

        val c = o.x * o.x - o.y * o.y + o.z * o.z
        if (abs(a) < epsilon) {
            val t = -c / (2.0 * b)
            val y = o.y + t * d.y
            if (minimum < y && y < maximum) {
                xs.add(Intersection(t, this))
            }
        } else {
            val disc = b * b - 4 * a * c
            if (disc < 0) {
                // ray does not intersect the wall
                return
            }

            var t0 = (-b - sqrt(disc)) / (2 * a)
            var t1 = (-b + sqrt(disc)) / (2 * a)
            if (t0 > t1) {
                val temp = t0
                t0 = t1
                t1 = temp
            }

            val y0 = o.y + t0 * d.y
            if (minimum < y0 && y0 < maximum) {
                xs.add(Intersection(t0, this))
            }

            val y1 = o.y + t1 * d.y
            if (minimum < y1 && y1 < maximum) {
                xs.add(Intersection(t1, this))
            }
        }
    }

    override fun checkCap(localRay: Ray, t: Double): Boolean {
        val x = localRay.origin.x + t * localRay.direction.x
        val y = localRay.origin.y + t * localRay.direction.y
        val z = localRay.origin.z + t * localRay.direction.z
        return (x * x + z * z) <= y * y
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        var y = sqrt(localPoint.x * localPoint.x + localPoint.z * localPoint.z)
        if (localPoint.y > 0) {
            y = -y
        }
        return vector(localPoint.x, y, localPoint.z)
    }

    override fun toStringName(): String {
        return "Cone"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cone) return false
        if (!super.equals(other)) return false
        return true
    }
}