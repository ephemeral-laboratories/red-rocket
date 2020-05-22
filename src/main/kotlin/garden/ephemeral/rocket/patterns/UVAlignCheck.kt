package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple

class UVAlignCheck(val main: Tuple, val ul: Tuple, val ur: Tuple, val bl: Tuple, val br: Tuple): UVPattern() {
    override fun uvPatternAt(u: Double, v: Double): Tuple {
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