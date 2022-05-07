package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.immutableDoubleArrayOf
import kotlin.math.pow
import kotlin.math.roundToInt

abstract class Color {
    abstract val isBlack: Boolean
    val isNonBlack: Boolean get() = !isBlack

    abstract operator fun plus(their: Color): Color

    abstract operator fun minus(their: Color): Color

    abstract operator fun times(scalar: Double): Color

    abstract operator fun times(their: Color): Color

    abstract fun toLinearRgbDoubles(): ImmutableDoubleArray

    abstract fun toCieXyz(): CieXyzColor

    fun toSRgbDoubles(): ImmutableDoubleArray {
        fun convert(v: Double): Double {
            return if (v <= 0.0031308) {
                12.92 * v
            } else {
                1.055 * v.pow(1 / 2.4) - 0.055
            }
        }
        val (r, g, b) = toLinearRgbDoubles()
        return immutableDoubleArrayOf(convert(r), convert(g), convert(b))
    }

    fun toSRgbInts(): IntArray {
        fun convert(v: Double): Int {
            return (v * 255).roundToInt().coerceIn(0, 255)
        }
        val (r, g, b) = toSRgbDoubles()
        return intArrayOf(convert(r), convert(g), convert(b))
    }

    companion object {
        val black: Color = RgbColor(0.0, 0.0, 0.0)
        val white: Color = RgbColor(1.0, 1.0, 1.0)

        fun grey(v: Double): Color = RgbColor(v, v, v)

        fun srgb(r: Double, g: Double, b: Double): Color {
            fun convert(v: Double): Double {
                return if (v < 0.04045) {
                    v / 12.92
                } else {
                    ((v + 0.055) / 1.055).pow(2.4)
                }
            }
            return RgbColor(convert(r), convert(g), convert(b))
        }

        fun fromSRgbInts(r: Int, g: Int, b: Int, scale: Int = 255): Color {
            fun convert(v: Int): Double {
                return v / scale.toDouble()
            }
            return srgb(convert(r), convert(g), convert(b))
        }

        fun linearRgb(r: Double, g: Double, b: Double): Color {
            return RgbColor(r, g, b)
        }

        fun cieXyz(x: Double, y: Double, z: Double): Color {
            return CieXyzColor(x, y, z)
        }
    }
}
