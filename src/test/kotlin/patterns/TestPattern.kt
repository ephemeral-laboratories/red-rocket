package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import garden.ephemeral.rocket.spectra.DoubleSpectrum

class TestPattern : Pattern() {
    override fun patternAt(patternPoint: Tuple): DoubleSpectrum {
        return DoubleSpectrum.recoverFromCieXyzReflectance(
            linearRgb(patternPoint.x, patternPoint.y, patternPoint.z).toCieXyz()
        )
    }
}
