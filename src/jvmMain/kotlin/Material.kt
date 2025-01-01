package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.white
import garden.ephemeral.rocket.patterns.Pattern
import garden.ephemeral.rocket.shapes.Shape
import kotlin.math.pow

data class Material(
    val color: Color,
    val ambient: Color,
    val diffuse: Color,
    val specular: Color,
    val shininess: Double,
    val reflective: Color,
    val transparency: Color,
    val refractiveIndex: Double,
    val pattern: Pattern?,
    val emission: Color,
    val dissolve: Double,
    val illuminationModel: Int
) {
    companion object {
        val default = Material(
            white, grey(0.1), grey(0.9), grey(0.9), 200.0,
            black, black, 1.0, null, black, 1.0, -1
        )

        fun build(callback: Builder.() -> Unit): Material {
            return default.build(callback)
        }

        fun builder(): Builder {
            return Builder(default)
        }
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
    ): Color {
        val color = pattern?.patternAtShape(shape, worldPoint) ?: color

        // Combine the surface color with the light's color/intensity
        val effectiveColor = color * light.intensity

        // Direction to the light source
        val lightDirection = (light.position - worldPoint).normalize()

        // Ambient contribution
        var result = effectiveColor * ambient

        if (!inShadow) {
            // lightDotNormal represents the cosine of the angle between the
            // light vector and the normal vector . A negative number means the
            // light is on the other side of the surface.
            val lightDotNormal = lightDirection.dot(worldNormal)

            val diffuseColor: Color
            val specularColor: Color

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

        result += emission

        return result
    }

    class Builder(material: Material) {
        var color: Color = material.color
        var ambient: Color = material.ambient
        var diffuse: Color = material.diffuse
        var specular: Color = material.specular
        var shininess: Double = material.shininess
        var reflective: Color = material.reflective
        var transparency: Color = material.transparency
        var refractiveIndex: Double = material.refractiveIndex
        var pattern: Pattern? = material.pattern
        var emission: Color = material.emission
        var dissolve: Double = 0.0
        var illuminationModel: Int = 0

        fun build(): Material {
            return Material(
                color, ambient, diffuse, specular, shininess,
                reflective, transparency, refractiveIndex, pattern,
                emission, dissolve, illuminationModel
            )
        }

        fun build(callback: Builder.() -> Unit): Material {
            callback(this)
            return build()
        }
    }
}
