package garden.ephemeral.rocket.spectra

data class SpectralShape(
    val min: Double,
    val max: Double,
    val step: Double
) {
    init {
        if (min <= 0) {
            throw IllegalArgumentException("Min must be positive but was: $min")
        }
        if (max <= 0) {
            throw IllegalArgumentException("max must be positive but was: $max")
        }
        if (step <= 0) {
            throw IllegalArgumentException("Step must be positive but was: $step")
        }
        if (min >= max) {
            throw IllegalArgumentException("Min ($min) must be less than max ($max)")
        }
    }

    val wavelengths = generateSequence(min) { x -> x + step }
        .takeWhile { x -> x <= max }
        .toList()

    val size get() = wavelengths.size

    companion object {
        val Default = SpectralShape(380.0, 780.0, 10.0)
    }
}
