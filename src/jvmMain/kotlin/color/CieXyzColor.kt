package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.immutableDoubleArrayOf

data class CieXyzColor(val x: Double, val y: Double, val z: Double) : Color() {
    override val isBlack: Boolean get() = x == 0.0 && y == 0.0 && z == 0.0

    override operator fun plus(their: Color): CieXyzColor {
        if (their is CieXyzColor) {
            return CieXyzColor(x + their.x, y + their.y, z + their.z)
        }
        return this + their.toCieXyz()
    }

    override operator fun minus(their: Color): CieXyzColor {
        if (their is CieXyzColor) {
            return CieXyzColor(x - their.x, y - their.y, z - their.z)
        }
        return this - their.toCieXyz()
    }

    override operator fun times(scalar: Double): CieXyzColor {
        return CieXyzColor(x * scalar, y * scalar, z * scalar)
    }

    override operator fun times(their: Color): CieXyzColor {
        if (their is CieXyzColor) {
            return CieXyzColor(x * their.x, y * their.y, z * their.z)
        }
        return this * their.toCieXyz()
    }

    override fun toCieXyz(): CieXyzColor {
        return this
    }

    fun toLinearRgb(): RgbColor {
        val (r, g, b) = ColorTransforms.cieXyzToLinearRgb(immutableDoubleArrayOf(x, y, z))
        return RgbColor(r, g, b)
    }

    override fun toLinearRgbDoubles(): ImmutableDoubleArray {
        return ColorTransforms.cieXyzToLinearRgb(immutableDoubleArrayOf(x, y, z))
    }
}
