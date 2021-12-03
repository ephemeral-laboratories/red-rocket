package garden.ephemeral.rocket.shapes

import garden.ephemeral.rocket.*
import garden.ephemeral.rocket.util.ToStringBuilder

class CSG(val operation: Operation, val left: Shape, val right: Shape) : Shape() {
    init {
        left.parent = this
        right.parent = this
    }

    override fun localIntersect(localRay: Ray): Intersections {
        val intersections = merge(left.intersect(localRay), right.intersect(localRay))

        // XXX: For some reason we have to do this as a `List`. When I make an equivalent
        //      method for `Sequence` the wrong number of results comes out somehow.
        return filterIntersections(intersections)
            .asSequence()
            .toIntersections()
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        throw IllegalStateException("should not be asking a CSG for a normal!")
    }

    fun filterIntersections(intersections: List<Intersection>): List<Intersection> {
        // begin outside both children
        var inL = false
        var inR = false

        return buildList {
            intersections.forEach { intersection ->
                val lHit = left.includes(intersection.obj)
                if (operation.intersectionAllowed(lHit, inL, inR)) {
                    add(intersection)
                }

                // depending on which object was hit, toggle either inl or inr
                if (lHit) {
                    inL = !inL
                } else {
                    inR = !inR
                }
            }
        }
    }

    override fun includes(shape: Shape): Boolean {
        return left.includes(shape) || right.includes(shape)
    }

    override fun toStringImpl(builder: ToStringBuilder) {
        super.toStringImpl(builder)
        builder.add(::operation)
            .add(::left)
            .add(::right)
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
