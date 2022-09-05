package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import kotlin.math.floor

class UVCheckers(val width: Int, val height: Int, val a: DoubleSpectrum, val b: DoubleSpectrum) : UVPattern() {
    constructor(width: Int, height: Int, a: Color, b: Color) : this(
        width, height,
        DoubleSpectrum.recoverFromCieXyzReflectance(a.toCieXyz()),
        DoubleSpectrum.recoverFromCieXyzReflectance(b.toCieXyz())
    )

    override fun uvPatternAt(u: Double, v: Double): DoubleSpectrum {
        return if (
            (floor(u * width).toInt() + floor(v * height).toInt()) % 2 == 0
        ) {
            a
        } else {
            b
        }
    }
}
