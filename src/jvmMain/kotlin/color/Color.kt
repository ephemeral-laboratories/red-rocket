package garden.ephemeral.rocket.color

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.pow
import kotlin.math.roundToInt

abstract class Color {
    abstract val isBlack: Boolean
    val isNonBlack: Boolean get() = !isBlack

    abstract operator fun plus(their: Color): Color

    abstract operator fun minus(their: Color): Color

    abstract operator fun times(scalar: Double): Color

    abstract operator fun times(their: Color): Color

    abstract fun toLinearRgbDoubles(): D1Array<Double>

    abstract fun toCieXyz(): CieXyzColor

    fun toSRgbDoubles(): D1Array<Double> {
        fun convert(v: Double): Double {
            return if (v <= 0.0031308) {
                12.92 * v
            } else {
                1.055 * v.pow(1 / 2.4) - 0.055
            }
        }
        val rgb = toLinearRgbDoubles()
        return mk.ndarray(mk[convert(rgb[0]), convert(rgb[1]), convert(rgb[2])])
    }

    fun toSRgbInts(): IntArray {
        fun convert(v: Double): Int {
            return (v * 255).roundToInt().coerceIn(0, 255)
        }
        val rgb = toSRgbDoubles()
        return intArrayOf(convert(rgb[0]), convert(rgb[1]), convert(rgb[2]))
    }

    companion object {
        val black: Color = linearRgb(0.0, 0.0, 0.0)
        val white: Color = linearRgb(1.0, 1.0, 1.0)

        fun grey(v: Double): Color = linearRgb(v, v, v)

        fun srgb(r: Double, g: Double, b: Double): Color {
            fun convert(v: Double): Double {
                return if (v < 0.04045) {
                    v / 12.92
                } else {
                    ((v + 0.055) / 1.055).pow(2.4)
                }
            }
            return linearRgb(convert(r), convert(g), convert(b))
        }

        fun fromSRgbInts(r: Int, g: Int, b: Int, scale: Int = 255): Color {
            fun convert(v: Int): Double {
                return v / scale.toDouble()
            }
            return srgb(convert(r), convert(g), convert(b))
        }

        fun linearRgb(r: Double, g: Double, b: Double): Color {
            return RgbColor(mk.ndarray(mk[r, g, b]))
        }

        fun cieXyz(x: Double, y: Double, z: Double): Color {
            return CieXyzColor(mk.ndarray(mk[x, y, z]))
        }
    }
}
