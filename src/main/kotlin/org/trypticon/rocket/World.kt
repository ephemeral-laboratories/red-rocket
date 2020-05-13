package org.trypticon.rocket

import org.trypticon.rocket.Transforms.Companion.scaling
import org.trypticon.rocket.Tuple.Companion.black
import org.trypticon.rocket.Tuple.Companion.color
import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.white
import org.trypticon.rocket.shapes.Shape
import org.trypticon.rocket.shapes.Sphere
import kotlin.math.sqrt

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

    fun isShadowed(point: Tuple, light: PointLight): Boolean {
        val v = light.position - point
        val distance = v.magnitude
        val direction = v.normalize()
        val r = Ray(point, direction)
        val intersections = intersect(r)
        val h = Intersection.hit(intersections)
        return h != null && h.t < distance
    }

    fun shadeHit(precomputed: Intersection.Precomputed, recursionsAllowed: Int = 5): Tuple {
        return lights.fold(black) { color, light ->
            val material = precomputed.obj.material
            val shadowed = isShadowed(precomputed.overPoint, light)
            val surface = material.lighting(
                precomputed.obj, light, precomputed.overPoint, precomputed.eyeVector, precomputed.normal, shadowed)

            val reflected = reflectedColor(precomputed, recursionsAllowed)
            val refracted = refractedColor(precomputed, recursionsAllowed)

            val reflectRefract = if (material.reflective > 0.0 && material.transparency > 0.0) {
                val reflectance = precomputed.schlick()
                reflected * reflectance + refracted * (1.0 - reflectance)
            } else {
                reflected + refracted
            }

            color + surface + reflectRefract
        }
    }

    fun colorAt(ray: Ray, recursionsAllowed: Int = 5): Tuple {
        val intersections = intersect(ray)
        val hit = Intersection.hit(intersections) ?: return black
        val precomputed = hit.prepareComputations(ray, intersections)
        return shadeHit(precomputed, recursionsAllowed)
    }

    fun reflectedColor(comps: Intersection.Precomputed, recursionsAllowed: Int = 5): Tuple {
        if (recursionsAllowed == 0) {
            return black
        }

        if (comps.obj.material.reflective == 0.0) {
            return black
        }

        val reflectRay = Ray(comps.overPoint, comps.reflectVector)
        val color = colorAt(reflectRay, recursionsAllowed - 1)
        return color * comps.obj.material.reflective
    }

    fun refractedColor(comps: Intersection.Precomputed, recursionsAllowed: Int = 5): Tuple {
        if (recursionsAllowed == 0) {
            return black
        }

        if (comps.obj.material.transparency == 0.0) {
            return black
        }

        // Find the ratio of first index of refraction to the second.
        // (Yup, this is inverted from the definition of Snell's Law.)
        val nRatio = comps.n1 / comps.n2
        // cos(theta_i) is the same as the dot product of the two vectors
        val cosI = comps.eyeVector.dot(comps.normal)
        // Find sin(theta_t)^2 via trigonometric identity
        val sin2t = nRatio * nRatio * (1 - cosI * cosI)
        if (sin2t > 1.0) {
            // Total internal reflection
            return black
        }

        // Find cos(theta_t) via trigonometric identity
        val cosT = sqrt(1.0 - sin2t)
        val direction = comps.normal * (nRatio * cosI - cosT) - comps.eyeVector * nRatio
        val refractRay = Ray(comps.underPoint, direction)
        // Find the color of the refracted ray, making sure to multiply
        // by the transparency value to account for any opacity
        return colorAt(refractRay, recursionsAllowed - 1) * comps.obj.material.transparency
    }
}
