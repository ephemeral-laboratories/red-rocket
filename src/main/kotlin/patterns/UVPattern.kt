package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.spectra.DoubleSpectrum

abstract class UVPattern {
    abstract fun uvPatternAt(u: Double, v: Double): DoubleSpectrum
}
