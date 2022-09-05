package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import kotlin.math.floor

class GradientPattern(val a: DoubleSpectrum, val b: DoubleSpectrum) : Pattern() {
    constructor(a: Color, b: Color) : this(
        DoubleSpectrum.recoverFromCieXyzReflectance(a.toCieXyz()),
        DoubleSpectrum.recoverFromCieXyzReflectance(b.toCieXyz())
    )

    override fun patternAt(patternPoint: Tuple): DoubleSpectrum {
        val distance = b - a
        val fraction = patternPoint.x - floor(patternPoint.x)
        return a + distance * fraction
    }
}
