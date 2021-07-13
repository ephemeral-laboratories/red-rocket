package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.Matrix

object ColorTransforms {
    private val CIE_XYZ_TO_LINEAR_RGB = Matrix(3, 3, doubleArrayOf(
        3.2404542, -1.5371385, -0.4985314,
        -0.9692660, 1.8760108, 0.0415560,
        0.0556434, -0.2040259, 1.0572252
    ))

    private val LINEAR_RGB_TO_CIE_XYZ = Matrix(3, 3, doubleArrayOf(
        0.4124564, 0.3575761, 0.1804375,
        0.2126729, 0.7151522, 0.0721750,
        0.0193339, 0.1191920, 0.9503041
    ))

    fun cieXyzToLinearRgb(components: DoubleArray): DoubleArray = CIE_XYZ_TO_LINEAR_RGB.times(components)

    fun linearRgbToCieXyz(components: DoubleArray): DoubleArray = LINEAR_RGB_TO_CIE_XYZ.times(components)
}