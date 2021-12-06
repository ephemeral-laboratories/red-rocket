package garden.ephemeral.rocket.util

import kotlin.math.*

data class Complex(val real: Double, val imaginary: Double = 0.0) {
    val conjugate: Complex by lazy { Complex(real, -imaginary) }
    val isZero: Boolean = this == Zero

    val squaredMagnitude: Double by lazy { real.pow(2) + imaginary.pow(2) }
    val magnitude: Double by lazy { sqrt(squaredMagnitude) }
    val argument: Angle by lazy { atan2(imaginary, real).rad }

    operator fun plus(addend: Complex): Complex = Complex(real + addend.real, imaginary + addend.imaginary)
    operator fun plus(addend: Double): Complex = Complex(real + addend, imaginary)

    operator fun minus(subtrahend: Complex): Complex = Complex(real - subtrahend.real, imaginary - subtrahend.imaginary)
    operator fun minus(subtrahend: Double): Complex = Complex(real - subtrahend, imaginary)

    operator fun times(multiplier: Complex): Complex = Complex(
        real * multiplier.real - imaginary * multiplier.imaginary,
        real * multiplier.imaginary + imaginary * multiplier.real
    )

    operator fun times(multiplier: Double): Complex = Complex(real * multiplier, imaginary * multiplier)

    operator fun div(divisor: Complex): Complex {
        if (divisor.isZero) {
            throw IllegalArgumentException("Not handling dividing by zero right now, let me know what it should do")
        }

        var top: Complex = this
        var bottom: Complex = divisor

        if (bottom.imaginary != 0.0) {
            // Complex case, multiply top and bottom by the conjugate of the divisor,
            // making the bottom always real.
            top = this * divisor.conjugate
            bottom = divisor * divisor.conjugate
            assert(bottom.imaginary == 0.0)
        }

        return top / bottom.real
    }

    operator fun div(divisor: Double): Complex = Complex(real / divisor, imaginary / divisor)

    fun squared(): Complex {
        return times(this)
    }

    fun pow(value: Double): Complex {
        // Complex number (a + bi) rewritten in polar form, {r = magnitude, θ = argument}
        // is equivalent to the exponential form, a + bi = r e^(iθ)
        // Then we take that to the power n, getting
        // (a + bi)^n = (r e^(iθ))^n
        //            = (r^n) (e^(iθ))^n
        //            = (r^n) e^(inθ)
        val resultR = magnitude.pow(value)
        val resultTheta = argument * value
        return fromPolar(resultR, resultTheta)
    }

    override fun toString(): String {
        val builder = StringBuilder("complex(")
        if (real != 0.0 || imaginary == 0.0) {
            builder.append(real)
        }
        if (imaginary != 0.0) {
            if (real != 0.0) {
                if (imaginary.sign < 0) {
                    builder.append(" - ")
                    builder.append(-imaginary)
                } else {
                    builder.append(" + ")
                    builder.append(imaginary)
                }
            } else {
                builder.append(imaginary)
            }
            builder.append('i')
        }
        builder.append(")")
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Complex) return false

        // Default equality for doubles treats 0.0 and -0.0 as different
        // which forces us to implement equality ourselves
        if (real != other.real) return false
        if (imaginary != other.imaginary) return false

        return true
    }

    override fun hashCode(): Int {
        return 31 * real.hashCode() + imaginary.hashCode()
    }

    companion object {
        val Zero = Complex(0.0, 0.0)

        /**
         * Gets a complex number by its polar coordinates {r, θ}
         *
         * XXX: Is it worth also keeping the original values? Some formulae convert back to polar.
         *
         * @param r the radius. AKA `magnitude`.
         * @param theta the angle. AKA `argument`.
         */
        fun fromPolar(r: Double, theta: Angle): Complex {
            return Complex(r * cos(theta), r * sin(theta))
        }
    }
}

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

operator fun Double.plus(other: Complex): Complex = Complex(this) + other
operator fun Double.minus(other: Complex): Complex = Complex(this) - other
operator fun Double.times(other: Complex): Complex = Complex(this) * other
operator fun Double.div(other: Complex): Complex = Complex(this) / other

}

fun sqrt(value: Complex): Complex = value.pow(0.5)

val Double.i: Complex get() = Complex(0.0, this)
