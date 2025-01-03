package garden.ephemeral.rocket.util

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@Suppress("INLINE_CLASS_DEPRECATED")
inline class Angle(val radians: Double) {
    operator fun plus(addend: Angle): Angle = Angle(radians + addend.radians)

    operator fun minus(subtrahend: Angle): Angle = Angle(radians - subtrahend.radians)

    operator fun times(multiplier: Double): Angle = Angle(radians * multiplier)
    operator fun times(multiplier: Number): Angle = times(multiplier.toDouble())

    operator fun div(divisor: Double): Angle = Angle(radians / divisor)
    operator fun div(divisor: Number): Angle = div(divisor.toDouble())
}

private const val DEGREES_TO_RADIANS = 0.017453292519943295

val Double.deg get() = Angle(this * DEGREES_TO_RADIANS)
val Number.deg get() = toDouble().deg

val Double.rad get() = Angle(this)

fun sin(angle: Angle): Double = sin(angle.radians)
fun cos(angle: Angle): Double = cos(angle.radians)
fun tan(angle: Angle): Double = tan(angle.radians)

operator fun Number.times(angle: Angle) = angle * this
