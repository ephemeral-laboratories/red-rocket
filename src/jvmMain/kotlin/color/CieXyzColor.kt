package garden.ephemeral.rocket.color

import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.all
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times

data class CieXyzColor(val xyz: D1Array<Double>) : Color() {
    override val isBlack: Boolean get() = xyz.all { it == 0.0 }

    val x get() = xyz[0]
    val y get() = xyz[1]
    val z get() = xyz[2]

    override operator fun plus(their: Color): CieXyzColor {
        if (their is CieXyzColor) {
            return CieXyzColor(xyz + their.xyz)
        }
        return this + their.toCieXyz()
    }

    override operator fun minus(their: Color): CieXyzColor {
        if (their is CieXyzColor) {
            return CieXyzColor(xyz - their.xyz)
        }
        return this - their.toCieXyz()
    }

    override operator fun times(scalar: Double): CieXyzColor {
        return CieXyzColor(xyz * scalar)
    }

    override operator fun times(their: Color): CieXyzColor {
        if (their is CieXyzColor) {
            return CieXyzColor(xyz * their.xyz)
        }
        return this * their.toCieXyz()
    }

    override fun toCieXyz(): CieXyzColor {
        return this
    }

    fun toLinearRgb(): RgbColor {
        val rgb = ColorTransforms.cieXyzToLinearRgb(xyz)
        return RgbColor(rgb)
    }

    override fun toLinearRgbDoubles(): D1Array<Double> {
        return ColorTransforms.cieXyzToLinearRgb(xyz)
    }
}
