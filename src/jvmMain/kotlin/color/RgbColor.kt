package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.immutableDoubleArrayOf

data class RgbColor(val r: Double, val g: Double, val b: Double) : Color() {
    override val isBlack: Boolean get() = r == 0.0 && g == 0.0 && b == 0.0

    override operator fun plus(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(r + their.r, g + their.g, b + their.b)
        }
        return toCieXyz() + their.toCieXyz()
    }

    override operator fun minus(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(r - their.r, g - their.g, b - their.b)
        }
        return toCieXyz() - their.toCieXyz()
    }

    override operator fun times(scalar: Double): Color {
        return RgbColor(r * scalar, g * scalar, b * scalar)
    }

    override operator fun times(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(r * their.r, g * their.g, b * their.b)
        }
        return toCieXyz() * their.toCieXyz()
    }

    override fun toCieXyz(): CieXyzColor {
        val (x, y, z) = ColorTransforms.linearRgbToCieXyz(immutableDoubleArrayOf(r, g, b))
        return CieXyzColor(x, y, z)
    }

    override fun toLinearRgbDoubles(): ImmutableDoubleArray {
        return immutableDoubleArrayOf(r, g, b)
    }
}
