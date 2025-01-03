package garden.ephemeral.rocket.util

operator fun Double.plus(other: Complex): Complex = Complex(this) + other
operator fun Double.minus(other: Complex): Complex = Complex(this) - other
operator fun Double.times(other: Complex): Complex = Complex(this) * other
operator fun Double.div(other: Complex): Complex = Complex(this) / other

operator fun Number.plus(other: Complex): Complex = toDouble() + other
operator fun Number.minus(other: Complex): Complex = toDouble() - other
operator fun Number.times(other: Complex): Complex = toDouble() * other
operator fun Number.div(other: Complex): Complex = toDouble() / other

val Double.i: Complex get() = Complex(0.0, this)
val Number.i: Complex get() = toDouble().i

val i = Complex.ImaginaryUnit
