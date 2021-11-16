package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.color.Color

class UVAlignCheck(val main: Color, val ul: Color, val ur: Color, val bl: Color, val br: Color) : UVPattern() {
    override fun uvPatternAt(u: Double, v: Double): Color {
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
