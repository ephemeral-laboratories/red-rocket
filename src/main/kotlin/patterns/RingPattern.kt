package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import kotlin.math.floor
import kotlin.math.sqrt

class RingPattern(val a: DoubleSpectrum, val b: DoubleSpectrum) : Pattern() {
    constructor(a: Color, b: Color) : this(
        DoubleSpectrum.recoverFromCieXyzReflectance(a.toCieXyz()),
        DoubleSpectrum.recoverFromCieXyzReflectance(b.toCieXyz())
    )

    override fun patternAt(patternPoint: Tuple): DoubleSpectrum {
        return if (floor(sqrt(patternPoint.x * patternPoint.x + patternPoint.z * patternPoint.z)).toInt() % 2 == 0) {
            a
        } else {
            b
        }
    }
}
