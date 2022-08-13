package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum

data class PointLight(val position: Tuple, val intensity: Color) {
    val intensitySpectrum by lazy { DoubleSpectrum.recoverFromCieXyz(intensity.toCieXyz()) }
}
