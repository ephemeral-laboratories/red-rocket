package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.spectra.DoubleSpectrum

class TextureMap(val uvPattern: UVPattern, val uvMap: UVMap) : Pattern() {
    override fun patternAt(patternPoint: Tuple): DoubleSpectrum {
        val (u, v) = uvMap(patternPoint)
        return uvPattern.uvPatternAt(u, v)
    }
}
