package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.white
import garden.ephemeral.rocket.patterns.Pattern
import garden.ephemeral.rocket.shapes.Shape
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.Wavelength
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
    val ambientSpectrum: DoubleSpectrum by lazy { DoubleSpectrum.recoverFromCieXyzReflectance(ambient.toCieXyz()) }
    val diffuseSpectrum: DoubleSpectrum by lazy { DoubleSpectrum.recoverFromCieXyzReflectance(diffuse.toCieXyz()) }
    val specularSpectrum: DoubleSpectrum by lazy { DoubleSpectrum.recoverFromCieXyzReflectance(specular.toCieXyz()) }
    val reflectiveSpectrum: DoubleSpectrum by lazy { DoubleSpectrum.recoverFromCieXyzReflectance(reflective.toCieXyz()) }
    val transparencySpectrum: DoubleSpectrum by lazy { DoubleSpectrum.recoverFromCieXyzReflectance(transparency.toCieXyz()) }

    // XXX: Beware of this one, we haven't pinned down units for it, so it isn't going to behave.
    val emissionSpectrum: DoubleSpectrum by lazy { DoubleSpectrum.recoverFromCieXyzEmission(emission.toCieXyz()) }

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

        // FIXME: This should be applied once total, not once per light
        result += emission

        return result
    }

    fun lighting2(
        wavelength: Wavelength,
        shape: Shape,
        light: PointLight,
        worldPoint: Tuple,
        worldEyeDirection: Tuple,
        worldNormal: Tuple,
        inShadow: Boolean
    ): Double {
        // TODO: Push spectrum down into pattern code
        val surface = DoubleSpectrum.recoverFromCieXyzReflectance(
            (pattern?.patternAtShape(shape, worldPoint) ?: color).toCieXyz()
        )[wavelength]

        // Combine the surface color with the light's color/intensity
        val lightIntensity = light.intensitySpectrum[wavelength]
        val effectiveIntensity = surface * lightIntensity

        // Direction to the light source
        val lightDirection = (light.position - worldPoint).normalize()

        // Ambient contribution
        var result = effectiveIntensity * ambientSpectrum[wavelength]

        if (!inShadow) {
            // lightDotNormal represents the cosine of the angle between the
            // light vector and the normal vector . A negative number means the
            // light is on the other side of the surface.
            val lightDotNormal = lightDirection.dot(worldNormal)

            val diffuseIntensity: Double
            val specularIntensity: Double

            if (lightDotNormal < 0) {
                diffuseIntensity = 0.0
                specularIntensity = 0.0
            } else {
                // compute the diffuse contribution
                diffuseIntensity = effectiveIntensity * diffuseSpectrum[wavelength] * lightDotNormal
                // reflect_dot_eye represents the cosine of the angle between the
                // reflection vector and the eye vector . A negative number means the
                // light reflects away from the eye .
                val reflectDirection = (-lightDirection).reflect(worldNormal)
                val reflectDotEye = reflectDirection.dot(worldEyeDirection)

                specularIntensity = if (reflectDotEye <= 0) {
                    0.0
                } else {
                    // compute the specular contribution
                    val factor = reflectDotEye.pow(shininess)
                    lightIntensity * specularSpectrum[wavelength] * factor
                }
            }

            result += diffuseIntensity + specularIntensity
        }

        // FIXME: This should be applied once total, not once per light
        result += emissionSpectrum[wavelength]

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
