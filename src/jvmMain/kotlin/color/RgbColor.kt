package garden.ephemeral.rocket.color

import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.all
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times

data class RgbColor(val rgb: D1Array<Double>) : Color() {
    override val isBlack: Boolean get() = rgb.all { it == 0.0 }

    val r get() = rgb[0]
    val g get() = rgb[1]
    val b get() = rgb[2]

    override operator fun plus(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(rgb + their.rgb)
        }
        return toCieXyz() + their.toCieXyz()
    }

    override operator fun minus(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(rgb - their.rgb)
        }
        return toCieXyz() - their.toCieXyz()
    }

    override operator fun times(scalar: Double): Color {
        return RgbColor(rgb * scalar)
    }

    override operator fun times(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(rgb * their.rgb)
        }
        return toCieXyz() * their.toCieXyz()
    }

    override fun toCieXyz(): CieXyzColor {
        val xyz = ColorTransforms.linearRgbToCieXyz(rgb)
        return CieXyzColor(xyz)
    }

    override fun toLinearRgbDoubles(): D1Array<Double> {
        return rgb
    }
}
