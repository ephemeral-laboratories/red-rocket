package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Tuple

/**
 * Stokes parameters
 * https://en.wikipedia.org/wiki/Stokes_parameters
 */
object Stokes {
    val Unpolarized = Tuple(1.0, 0.0, 0.0, 0.0)
    val LinearlyPolarizedHorizontal = Tuple(1.0, 1.0, 0.0, 0.0)
    val LinearlyPolarizedVertical = Tuple(1.0, -1.0, 0.0, 0.0)
    val LinearlyPolarizedPlus45 = Tuple(1.0, 0.0, 1.0, 0.0)
    val LinearlyPolarizedMinus45 = Tuple(1.0, 0.0, -1.0, 0.0)
    val CircularlyPolarizedRight = Tuple(1.0, 0.0, 0.0, 1.0)
    val CircularlyPolarizedLeft = Tuple(1.0, 0.0, 0.0, -1.0)
}