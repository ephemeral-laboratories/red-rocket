package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.patterns.Pattern
import garden.ephemeral.rocket.shapes.Shape
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.ReflectanceSpectra
import garden.ephemeral.rocket.spectra.Wavelength
import kotlin.math.pow

data class Material(
    val color: DoubleSpectrum,
    val ambient: DoubleSpectrum,
    val diffuse: DoubleSpectrum,
    val specular: DoubleSpectrum,
    val shininess: Double,
    val reflective: DoubleSpectrum,
    val transparency: DoubleSpectrum,
    val refractiveIndex: DoubleSpectrum,
    val pattern: Pattern?,
    val emission: DoubleSpectrum,
    val dissolve: Double,
    val illuminationModel: Int
) {
    val colorAsColor by lazy { color.toCieXyzReflectance() }
    val ambientAsColor by lazy { ambient.toCieXyzReflectance() }
    val diffuseAsColor by lazy { diffuse.toCieXyzReflectance() }
    val specularAsColor by lazy { specular.toCieXyzReflectance() }
    val emissionAsColor by lazy { emission.toCieXyzEmission() }
    val reflectiveAsColor by lazy { reflective.toCieXyzReflectance() }
    val transparencyAsColor by lazy { transparency.toCieXyzReflectance() }

    companion object {
        val default = Material(
            color = ReflectanceSpectra.White,
            ambient = ReflectanceSpectra.grey(0.1),
            diffuse = ReflectanceSpectra.grey(0.9),
            specular = ReflectanceSpectra.grey(0.9),
            shininess = 200.0,
            reflective = ReflectanceSpectra.Black,
            transparency = ReflectanceSpectra.Black,
            refractiveIndex = DoubleSpectrum.ofConstant(1.0),
            pattern = null,
            emission = ReflectanceSpectra.Black,
            dissolve = 1.0,
            illuminationModel = -1
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
        val color = pattern?.patternAtShape(shape, worldPoint)?.toCieXyzReflectance() ?: colorAsColor

        // Combine the surface color with the light's color/intensity
        val effectiveColor = color * light.intensityAsColor

        // Direction to the light source
        val lightDirection = (light.position - worldPoint).normalize()

        // Ambient contribution
        var result = effectiveColor * ambientAsColor

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
                diffuseColor = effectiveColor * diffuseAsColor * lightDotNormal
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
                    light.intensityAsColor * specularAsColor * factor
                }
            }

            result += diffuseColor + specularColor
        }

        // FIXME: This should be applied once total, not once per light
        result += emissionAsColor

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
        val surface = (pattern?.patternAtShape(shape, worldPoint) ?: color)[wavelength]

        // Combine the surface color with the light's color/intensity
        val lightIntensity = light.intensity[wavelength]
        val effectiveIntensity = surface * lightIntensity

        // Direction to the light source
        val lightDirection = (light.position - worldPoint).normalize()

        // Ambient contribution
        var result = effectiveIntensity * ambient[wavelength]

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
                diffuseIntensity = effectiveIntensity * diffuse[wavelength] * lightDotNormal
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
                    lightIntensity * specular[wavelength] * factor
                }
            }

            result += diffuseIntensity + specularIntensity
        }

        // FIXME: This should be applied once total, not once per light
        result += emission[wavelength]

        return result
    }

    class Builder(material: Material) {
        private var color: DoubleSpectrum = material.color
        private var ambient: DoubleSpectrum = material.ambient
        private var diffuse: DoubleSpectrum = material.diffuse
        private var specular: DoubleSpectrum = material.specular
        private var shininess: Double = material.shininess
        private var reflective: DoubleSpectrum = material.reflective
        private var transparency: DoubleSpectrum = material.transparency
        private var refractiveIndex: DoubleSpectrum = material.refractiveIndex
        private var pattern: Pattern? = material.pattern
        private var emission: DoubleSpectrum = material.emission
        private var dissolve: Double = 0.0
        private var illuminationModel: Int = 0

        fun color(color: Color): Builder {
            this.color = DoubleSpectrum.recoverFromCieXyzReflectance(color.toCieXyz())
            return this
        }

        fun color(spectrum: DoubleSpectrum): Builder {
            color = spectrum
            return this
        }

        fun ambient(color: Color): Builder {
            ambient = DoubleSpectrum.recoverFromCieXyzReflectance(color.toCieXyz())
            return this
        }

        fun ambient(spectrum: DoubleSpectrum): Builder {
            ambient = spectrum
            return this
        }

        fun diffuse(color: Color): Builder {
            diffuse = DoubleSpectrum.recoverFromCieXyzReflectance(color.toCieXyz())
            return this
        }

        fun diffuse(spectrum: DoubleSpectrum): Builder {
            diffuse = spectrum
            return this
        }

        fun specular(color: Color): Builder {
            specular = DoubleSpectrum.recoverFromCieXyzReflectance(color.toCieXyz())
            return this
        }

        fun specular(spectrum: DoubleSpectrum): Builder {
            specular = spectrum
            return this
        }

        fun pattern(pattern: Pattern): Builder {
            this.pattern = pattern
            return this
        }

        fun emission(color: Color): Builder {
            // XXX: Beware of this one, we haven't pinned down units for it, so it isn't going to behave.
            emission = DoubleSpectrum.recoverFromCieXyzEmission(color.toCieXyz())
            return this
        }

        fun emission(spectrum: DoubleSpectrum): Builder {
            emission = spectrum
            return this
        }

        fun shininess(value: Double): Builder {
            shininess = value
            return this
        }

        fun reflective(color: Color): Builder {
            reflective = DoubleSpectrum.recoverFromCieXyzReflectance(color.toCieXyz())
            return this
        }

        fun reflective(spectrum: DoubleSpectrum): Builder {
            reflective = spectrum
            return this
        }

        fun transparency(color: Color): Builder {
            transparency = DoubleSpectrum.recoverFromCieXyzReflectance(color.toCieXyz())
            return this
        }

        fun transparency(spectrum: DoubleSpectrum): Builder {
            transparency = spectrum
            return this
        }

        fun refractiveIndex(value: Double): Builder {
            refractiveIndex = DoubleSpectrum.ofConstant(value)
            return this
        }

        fun refractiveIndex(spectrum: DoubleSpectrum): Builder {
            refractiveIndex = spectrum
            return this
        }

        fun illuminationModel(value: Int): Builder {
            illuminationModel = value
            return this
        }

        fun dissolve(value: Double): Builder {
            dissolve = value
            return this
        }

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
