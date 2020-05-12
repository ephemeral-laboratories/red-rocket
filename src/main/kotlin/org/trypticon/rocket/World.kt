package org.trypticon.rocket

import org.trypticon.rocket.Lighting.Companion.lighting
import org.trypticon.rocket.Transforms.Companion.scaling
import org.trypticon.rocket.Tuple.Companion.black
import org.trypticon.rocket.Tuple.Companion.color
import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.white

class World {
    var objects : MutableList<Shape> = mutableListOf()
    var lights : MutableList<PointLight> = mutableListOf()

    companion object {
        fun defaultWorld(): World {
            return World().apply {
                lights.add(PointLight(point(-10.0, 10.0, -10.0), white))
                objects.add(Sphere().apply {
                    material.color = color(0.8, 1.0, 0.6)
                    material.diffuse = 0.7
                    material.specular = 0.2
                })
                objects.add(Sphere().apply {
                    transform = scaling(0.5, 0.5, 0.5)
                })
            }
        }
    }

    fun intersect(ray: Ray): List<Intersection> {
        return objects
            .flatMap { obj -> obj.intersect(ray) }
            .sortedBy { intersection -> intersection.t }
    }

    fun shadeHit(precomputed: Intersection.Precomputed): Tuple {
        return lights.fold(black) { color, light ->
            color + lighting(precomputed.obj.material, light, precomputed.point, precomputed.eyeVector, precomputed.normal)
        }
    }

    fun colorAt(ray: Ray): Tuple {
        val intersections = intersect(ray)
        val hit = Intersection.hit(intersections) ?: return black
        val precomputed = hit.prepareComputations(ray)
        return shadeHit(precomputed)
    }
}
