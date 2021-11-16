package garden.ephemeral.rocket.util

import kotlin.math.sign

data class Complex(val real: Double, val imaginary: Double) {
    val conjugate: Complex by lazy { Complex(real, -imaginary) }
    val isZero: Boolean = this == zero

    operator fun plus(addend: Complex): Any {
        return Complex(real + addend.real, imaginary + addend.imaginary)
    }

    operator fun minus(subtrahend: Complex): Complex {
        return Complex(real - subtrahend.real, imaginary - subtrahend.imaginary)
    }

    operator fun times(multiplier: Complex): Complex {
        return Complex(
            real * multiplier.real - imaginary * multiplier.imaginary,
            real * multiplier.imaginary + imaginary * multiplier.real
        )
    }

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

        return Complex(top.real / bottom.real, top.imaginary / bottom.real)
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
        val zero = Complex(0.0, 0.0)
    }
}
