package garden.ephemeral.rocket.util

import kotlin.math.sign

data class Complex(val real: Double, val imaginary: Double) {
    operator fun plus(addend: Complex): Any {
        return Complex(real + addend.real, imaginary + addend.imaginary)
    }

    operator fun minus(subtrahend: Complex): Complex {
        return Complex(real - subtrahend.real, imaginary - subtrahend.imaginary)
    }

    operator fun times(multiplier: Complex): Complex {
        return Complex(real * multiplier.real - imaginary * multiplier.imaginary,
            real * multiplier.imaginary + imaginary * multiplier.real)
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

}