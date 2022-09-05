package garden.ephemeral.rocket.spectra

object ReflectanceSpectra {
    val White = grey(1.0)
    val Black = grey(0.0)
    fun grey(value: Double) = DoubleSpectrum.ofConstant(value)
}

val DoubleSpectrum.isBlack get() = values.all { v -> v <= 0.0 }
val DoubleSpectrum.isNonBlack get() = values.any { v -> v > 0.0 }
