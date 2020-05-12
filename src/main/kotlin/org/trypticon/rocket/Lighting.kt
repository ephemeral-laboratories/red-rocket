package org.trypticon.rocket

import org.trypticon.rocket.Tuple.Companion.black
import kotlin.math.pow

class Lighting {
    companion object {
        fun lighting(material: Material, light: PointLight, point: Tuple, eyeDirection: Tuple, normal: Tuple): Tuple {
            // Combine the surface color with the light's color/intensity
            val effectiveColor: Tuple = material.color * light.intensity

            // Direction to the light source
            val lightDirection = (light.position - point).normalize()

            // Ambient contribution
            val ambient = effectiveColor * material.ambient

            // lightDotNormal represents the cosine of the angle between the
            // light vector and the normal vector . A negative number means the
            // light is on the other side of the surface.
            val lightDotNormal = lightDirection.dot(normal)

            val diffuse : Tuple
            val specular : Tuple

            if (lightDotNormal < 0) {
                diffuse = black
                specular = black
            } else {
                // compute the diffuse contribution
                diffuse = effectiveColor * material.diffuse * lightDotNormal
                // reflect_dot_eye represents the cosine of the angle between the
                // reflection vector and the eye vector . A negative number means the
                // light reflects away from the eye .
                val reflectDirection = (-lightDirection).reflect(normal)
                val reflectDotEye = reflectDirection.dot(eyeDirection)

                specular = if (reflectDotEye <= 0) {
                    black
                } else {
                    // compute the specular contribution
                    val factor = reflectDotEye.pow(material.shininess)
                    light.intensity * material.specular * factor
                }
            }

            // Add the three contributions together to get the final shading
            return ambient + diffuse + specular
        }
    }
}