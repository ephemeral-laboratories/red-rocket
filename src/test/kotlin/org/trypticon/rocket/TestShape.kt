package org.trypticon.rocket

import org.trypticon.rocket.Tuple.Companion.vector

class TestShape: Shape() {
    var savedRay: Ray? = null

    override fun localIntersect(localRay: Ray): List<Intersection> {
        savedRay = localRay
        return listOf()
    }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        return vector(localPoint.x, localPoint.y, localPoint.z)
    }

    override fun toStringName(): String {
        return "TestShape"
    }
}