package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.Intersection
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple

class CSG(val operation: CSG.Operation, val left: Shape, val right: Shape): Shape() {
    init {
        left.parent = this
        right.parent = this
    }

    override fun localIntersect(localRay: Ray): List<Intersection> {
        val intersections = (left.intersect(localRay) + right.intersect(localRay))
            .sortedBy { intersection -> intersection.t }
        return filterIntersections(intersections)
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        throw IllegalStateException("should not be asking a CSG for a normal!")
    }

    fun filterIntersections(intersections: List<Intersection>): List<Intersection> {
        // begin outside of both children
        var inL = false
        var inR = false

        val result = mutableListOf<Intersection>()
        intersections.forEach { intersection ->
            val lHit = left.includes(intersection.obj)
            if (operation.intersectionAllowed(lHit, inL, inR)) {
                result.add(intersection)
            }

            // depending on which object was hit, toggle either inl or inr
            if (lHit) {
                inL = !inL
            } else {
                inR = !inR
            }
        }
        return result
    }

    override fun includes(shape: Shape): Boolean {
        return left.includes(shape) || right.includes(shape)
    }

    override fun toStringName(): String {
        return "CSG"
    }

    override fun toStringParams(): String {
        return "${super.toStringParams()}, operation=$operation, left=$left, right=$right"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CSG) return false
        if (!super.equals(other)) return false

        if (operation != other.operation) return false
        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + operation.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }

    enum class Operation {
        UNION {
            override fun intersectionAllowed(lHit: Boolean, inL: Boolean, inR: Boolean): Boolean {
                return (lHit && !inR) || (!lHit && !inL)
            }
        },

        INTERSECTION {
            override fun intersectionAllowed(lHit: Boolean, inL: Boolean, inR: Boolean): Boolean {
                return (lHit && inR) || (!lHit && inL)
            }
        },

        DIFFERENCE {
            override fun intersectionAllowed(lHit: Boolean, inL: Boolean, inR: Boolean): Boolean {
                return (lHit && !inR) || (!lHit && inL)
            }
        };

        abstract fun intersectionAllowed(lHit: Boolean, inL: Boolean, inR: Boolean): Boolean
    }
}