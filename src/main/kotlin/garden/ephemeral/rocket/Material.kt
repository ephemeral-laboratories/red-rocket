package garden.ephemeral.rocket

import garden.ephemeral.rocket.Tuple.Companion.black
import garden.ephemeral.rocket.Tuple.Companion.grey
import garden.ephemeral.rocket.Tuple.Companion.white
import garden.ephemeral.rocket.patterns.Pattern
import garden.ephemeral.rocket.shapes.Shape
import kotlin.math.pow

data class Material(
    val color: Tuple,
    val ambient: Tuple,
    val diffuse: Tuple,
    val specular: Tuple,
    val shininess: Double,
    val reflective: Double,
    val transparency: Double,
    val refractiveIndex: Double,
    val pattern: Pattern?
) {
    companion object {
        class Builder(material: Material) {
            var color: Tuple            = material.color
            var ambient: Tuple          = material.ambient
            var diffuse: Tuple          = material.diffuse
            var specular: Tuple         = material.specular
            var shininess: Double       = material.shininess
            var reflective: Double      = material.reflective
            var transparency: Double    = material.transparency
            var refractiveIndex: Double = material.refractiveIndex
            var pattern: Pattern?       = material.pattern

            fun build(callback: Builder.() -> Unit): Material {
                callback(this)
                return Material(
                    color, ambient, diffuse, specular, shininess,
                    reflective, transparency, refractiveIndex, pattern)
            }
        }

        fun build(callback: Builder.() -> Unit) : Material {
            return default.build(callback)
        }

        val default: Material = Material(white, grey(0.1), grey(0.9), grey(0.9), 200.0, 0.0, 0.0, 1.0, null)
    }

    fun build(callback: Builder.() -> Unit): Material {
        return Builder(this).build(callback)
    }

    fun lighting(
        shape: Shape,
        light: PointLight,
        worldPoint: Tuple,
        worldEyeDirection: Tuple,
        worldNormal: Tuple,
        inShadow: Boolean
    ) : Tuple {

        val color = pattern?.patternAtShape(shape, worldPoint) ?: color

        // Combine the surface color with the light's color/intensity
        val effectiveColor: Tuple = color * light.intensity

        // Direction to the light source
        val lightDirection = (light.position - worldPoint).normalize()

        // Ambient contribution
        var result = effectiveColor * ambient

        if (!inShadow) {
            // lightDotNormal represents the cosine of the angle between the
            // light vector and the normal vector . A negative number means the
            // light is on the other side of the surface.
            val lightDotNormal = lightDirection.dot(worldNormal)

            val diffuseColor: Tuple
            val specularColor: Tuple

            if (lightDotNormal < 0) {
                diffuseColor = black
                specularColor = black
            } else {
                // compute the diffuse contribution
                diffuseColor = effectiveColor * diffuse * lightDotNormal
                // reflect_dot_eye represents the cosine of the angle between the
                // reflection vector and the eye vector . A negative number means the
                // light reflects away from the eye .
                val reflectDirection = (-lightDirection).reflect(worldNormal)
                val reflectDotEye = reflectDirection.dot(worldEyeDirection)

                specularColor = if (reflectDotEye <= 0) {
                    black
                } else {
                    // compute the specular contribution
                    val factor = reflectDotEye.pow(shininess)
                    light.intensity * specular * factor
                }
            }

            result += diffuseColor + specularColor
        }

        return result
    }
}