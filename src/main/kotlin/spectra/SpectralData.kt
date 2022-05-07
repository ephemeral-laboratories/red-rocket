package garden.ephemeral.rocket.spectra

/**
 * Holder for spectral data.
 *
 * For performance reasons, spectral data is not used directly, but converted to
 * a `Spectrum` for actual use, so that all spectra which are used for computation
 * are the same shape.
 */
sealed class SpectralData<S : Spectrum<*, S>> {

    /**
     * Creates a spectrum from the data.
     * Interpolates the data as needed to fit the given shape.
     *
     * @param shape the desired shape.
     * @return the spectrum.
     */
    abstract fun createSpectrum(shape: SpectralShape = SpectralShape.Default): S
}