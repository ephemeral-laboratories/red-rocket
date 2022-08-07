package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.shapes.Shape

class World {
    var objects = mutableListOf<Shape>()
    var lights = mutableListOf<PointLight>()

    fun intersect(ray: Ray): Intersections {
        return objects
            .asSequence()
            .flatMap { obj -> obj.intersect(ray) }
            .toIntersections()
    }

    fun isShadowed(point: Tuple, light: PointLight): Boolean {
        val v = light.position - point
        val distance = v.magnitude
        val direction = v.normalize()
        val r = Ray(point, direction)
        val intersections = intersect(r)
        val h = intersections.hit()
        return h != null && h.t < distance
    }

    fun shadeHit(precomputed: Intersection.Precomputed, recursionsAllowed: Int = 5): Color {
        return lights.fold(black) { color, light ->
            val material = precomputed.obj.material
            val shadowed = isShadowed(precomputed.overPoint, light)
            val surface = material.lighting(
                precomputed.obj,
                light,
                precomputed.overPoint,
                precomputed.eyeline,
                precomputed.normal,
                shadowed
            )

            val reflected = reflectedColor(precomputed, recursionsAllowed)
            val refracted = refractedColor(precomputed, recursionsAllowed)

            val reflectRefract = if (material.reflective.isNonBlack && material.transparency.isNonBlack) {
                val reflectance = precomputed.fresnel()
                reflected * reflectance + refracted * (1.0 - reflectance)
            } else {
                reflected + refracted
            }

            color + surface + reflectRefract
        }
    }

    fun colorAt(ray: Ray, recursionsAllowed: Int = 5): Color {
        val intersections = intersect(ray)
        val hit = intersections.hit() ?: return black
        val precomputed = hit.prepareComputations(ray, intersections)
        return shadeHit(precomputed, recursionsAllowed)
    }

    fun reflectedColor(comps: Intersection.Precomputed, recursionsAllowed: Int = 5): Color {
        if (recursionsAllowed == 0) {
            return black
        }

        if (comps.obj.material.reflective.isBlack) {
            return black
        }

        val reflectRay = Ray(comps.overPoint, comps.reflection)
        val color = colorAt(reflectRay, recursionsAllowed - 1)
        return color * comps.obj.material.reflective
    }

    fun refractedColor(comps: Intersection.Precomputed, recursionsAllowed: Int = 5): Color {
        if (recursionsAllowed == 0) {
            return black
        }

        if (comps.obj.material.transparency.isBlack) {
            return black
        }

        // Find the ratio of first index of refraction to the second.
        val nRatio = comps.n1 / comps.n2
        if (comps.sin2ThetaT > 1.0) {
            // Total internal reflection
            return black
        }

        val direction = comps.normal * (nRatio * comps.cosThetaI - comps.cosThetaT) - comps.eyeline * nRatio
        val refractRay = Ray(comps.underPoint, direction)
        // Find the color of the refracted ray, making sure to multiply
        // by the transparency value to account for any opacity
        return colorAt(refractRay, recursionsAllowed - 1) * comps.obj.material.transparency
    }
}
