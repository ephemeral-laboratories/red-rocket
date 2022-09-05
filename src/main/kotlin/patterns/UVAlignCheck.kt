package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum

class UVAlignCheck(
    val main: DoubleSpectrum,
    val ul: DoubleSpectrum,
    val ur: DoubleSpectrum,
    val bl: DoubleSpectrum,
    val br: DoubleSpectrum
) : UVPattern() {
    constructor(main: Color, ul: Color, ur: Color, bl: Color, br: Color) : this(
        DoubleSpectrum.recoverFromCieXyzReflectance(main.toCieXyz()),
        DoubleSpectrum.recoverFromCieXyzReflectance(ul.toCieXyz()),
        DoubleSpectrum.recoverFromCieXyzReflectance(ur.toCieXyz()),
        DoubleSpectrum.recoverFromCieXyzReflectance(bl.toCieXyz()),
        DoubleSpectrum.recoverFromCieXyzReflectance(br.toCieXyz())
    )

    override fun uvPatternAt(u: Double, v: Double): DoubleSpectrum {
        if (v > 0.8) {
            if (u < 0.2) {
                return ul
            }
            if (u > 0.8) {
                return ur
            }
        } else if (v < 0.2) {
            if (u < 0.2) {
                return bl
            }
            if (u > 0.8) {
                return br
            }
        }
        return main
    }
}
