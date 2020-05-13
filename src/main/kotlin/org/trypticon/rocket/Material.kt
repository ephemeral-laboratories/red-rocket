package org.trypticon.rocket

import kotlin.math.pow

data class Material(
    var color: Tuple = Tuple.color(1.0, 1.0, 1.0),
    var ambient: Double = 0.1,
    var diffuse: Double = 0.9,
    var specular: Double = 0.9,
    var shininess: Double = 200.0
) {
    fun lighting(light: PointLight, point: Tuple, eyeDirection: Tuple, normal: Tuple, inShadow: Boolean): Tuple {
        // Combine the surface color with the light's color/intensity
        val effectiveColor: Tuple = color * light.intensity

        // Direction to the light source
        val lightDirection = (light.position - point).normalize()

        // Ambient contribution
        var result = effectiveColor * ambient

        if (!inShadow) {
            // lightDotNormal represents the cosine of the angle between the
            // light vector and the normal vector . A negative number means the
            // light is on the other side of the surface.
            val lightDotNormal = lightDirection.dot(normal)

            val diffuseColor: Tuple
            val specularColor: Tuple

            if (lightDotNormal < 0) {
                diffuseColor = Tuple.black
                specularColor = Tuple.black
            } else {
                // compute the diffuse contribution
                diffuseColor = effectiveColor * diffuse * lightDotNormal
                // reflect_dot_eye represents the cosine of the angle between the
                // reflection vector and the eye vector . A negative number means the
                // light reflects away from the eye .
                val reflectDirection = (-lightDirection).reflect(normal)
                val reflectDotEye = reflectDirection.dot(eyeDirection)

                specularColor = if (reflectDotEye <= 0) {
                    Tuple.black
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