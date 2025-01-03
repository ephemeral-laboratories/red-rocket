package garden.ephemeral.rocket.util

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt

fun sqrt(value: Complex): Complex = value.pow(0.5)

/**
 * Complex square root for value which may be negative.
 * Considerably faster than creating a `Complex` just to square root it.
 *
 * @param value the input value.
 * @return the square root.
 */
fun complexSqrt(value: Double): Complex {
    return if (value >= 0.0) {
        Complex(sqrt(value), 0.0)
    } else {
        Complex(0.0, sqrt(-value))
    }
}

fun sin(value: Complex): Complex = Complex(
    sin(value.real) * cosh(value.imaginary),
    cos(value.real) * sinh(value.imaginary)
)

fun cos(value: Complex): Complex = Complex(
    cos(value.real) * cosh(value.imaginary),
    -sin(value.real) * sinh(value.imaginary)
)

fun tan(value: Complex): Complex {
    // Just sin(value) / cos(value) but we can compute the individual parts once instead of twice
    val cosR = cos(value.real)
    val sinR = sin(value.real)
    val coshI = cosh(value.imaginary)
    val sinhI = sinh(value.imaginary)
    return Complex(sinR * coshI, cosR * sinhI) / Complex(cosR * coshI, -sinR * sinhI)
}

fun ln(value: Complex): Complex {
    var result = Complex(ln(value.magnitude), atan2(value.imaginary, value.real))
    while (result.imaginary > PI) result -= 2 * PI
    while (result.imaginary <= -PI) result += 2 * PI
    return result
}

fun arcsin(value: Complex): Complex =
    -Complex.ImaginaryUnit * ln(sqrt(1.0 - value.pow(2)) + Complex.ImaginaryUnit * value)

fun arccos(value: Complex): Complex =
    (PI / 2) + Complex.ImaginaryUnit * ln(sqrt(1.0 - value.pow(2)) + Complex.ImaginaryUnit * value)

fun arctan(value: Complex): Complex =
    0.5 * Complex.ImaginaryUnit * ln(1.0 - Complex.ImaginaryUnit * value) -
            0.5 * Complex.ImaginaryUnit * ln(1.0 + Complex.ImaginaryUnit * value)
