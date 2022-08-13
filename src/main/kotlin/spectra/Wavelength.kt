package garden.ephemeral.rocket.spectra

/**
 * A single wavelength.
 *
 * Ties together the value of the wavelength in nanometres and its index in the
 * spectral shape currently being rendered.
 *
 * @property inNanometres the wavelength in nanometres
 * @property index the index of the wavelength in the spectral shape
 */
data class Wavelength(val inNanometres: Double, val index: Int) {
    /**
     * Gets the wavelength in metres.
     */
    val inMetres get() = inNanometres * 1E-9
}
