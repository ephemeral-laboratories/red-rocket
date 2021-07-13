package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.Tuple

class TextureMap(val uvPattern: UVPattern, val uvMap: (Tuple) -> UV): Pattern() {
    override fun patternAt(patternPoint: Tuple): Color {
        val (u, v) = uvMap(patternPoint)
        return uvPattern.uvPatternAt(u, v)
    }
}