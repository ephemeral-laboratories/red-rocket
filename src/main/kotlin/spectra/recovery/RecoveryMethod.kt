package garden.ephemeral.rocket.spectra.recovery

import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.spectra.DoubleSpectrum

/**
 * <p>Abstraction of spectral recovery method.</p>
 *
 * <p>Here because there are many potential recovery methods, so it would pay
 *    to make it easy to switch between them if we end up with more than one.</p>
 */
interface RecoveryMethod {

    /**
     * Recovers a spectrum for an XYZ color.
     *
     * @param color the color.
     * @return the recovered spectrum.
     */
    fun recoverSpectrum(color: CieXyzColor): DoubleSpectrum
}