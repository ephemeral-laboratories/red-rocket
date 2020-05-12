package org.trypticon.rocket

import org.trypticon.rocket.Matrix.Companion.identity4x4
import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.vector
import kotlin.math.sqrt

class Sphere {
    var transform: Matrix = identity4x4
    var material: Material = Material()

    fun intersect(ray: Ray): List<Intersection> {
        val ray2 = ray.transform(transform.inverse())

        val sphereToRay = ray2.origin - Tuple.point(0.0, 0.0, 0.0)
        val a = ray2.direction.dot(ray2.direction)
        val b = 2 * ray2.direction.dot(sphereToRay)
        val c = sphereToRay.dot(sphereToRay) - 1
        val discriminant = b * b - 4 * a * c

        if (discriminant < 0) {
            return emptyList()
        }

        val t1 = (-b - sqrt(discriminant)) / (2 * a)
        val t2 = (-b + sqrt(discriminant)) / (2 * a)
        return listOf(
            Intersection(t1, this),
            Intersection(t2, this)
        )
    }

    fun worldNormalAt(worldPoint: Tuple): Tuple {
        val objectPoint = transform.inverse() * worldPoint
        val objectNormal = objectPoint - point(0.0, 0.0, 0.0)
        val worldNormal = transform.inverse().transpose() * objectNormal
        return vector(worldNormal.x, worldNormal.y, worldNormal.z).normalize()
    }
}