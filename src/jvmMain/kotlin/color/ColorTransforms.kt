package garden.ephemeral.rocket.color

import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array

object ColorTransforms {
    private val CIE_XYZ_TO_LINEAR_RGB = mk.ndarray(
        mk[
            mk[3.2404542, -1.5371385, -0.4985314],
            mk[-0.9692660, 1.8760108, 0.0415560],
            mk[0.0556434, -0.2040259, 1.0572252],
        ]
    )

    private val LINEAR_RGB_TO_CIE_XYZ = mk.ndarray(
        mk[
            mk[0.4124564, 0.3575761, 0.1804375],
            mk[0.2126729, 0.7151522, 0.0721750],
            mk[0.0193339, 0.1191920, 0.9503041],
        ]
    )

    fun cieXyzToLinearRgb(components: D1Array<Double>): D1Array<Double> = CIE_XYZ_TO_LINEAR_RGB dot components

    fun linearRgbToCieXyz(components: D1Array<Double>): D1Array<Double> = LINEAR_RGB_TO_CIE_XYZ dot components
}
