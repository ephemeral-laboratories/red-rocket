package garden.ephemeral.rocket.spectra

import garden.ephemeral.rocket.util.ImmutableDoubleArray

/**
 * A spectrum of data at different wavelengths.
 *
 * @param shape the shape of the spectral data.
 * @param <T> the type of elements in the spectrum.
 * @param <S> the type of the spectrum itself.
 */
sealed class Spectrum<T, S : Spectrum<T, S>>(
    val shape: SpectralShape
) {
    val wavelengths get() = shape.wavelengths

    abstract operator fun plus(other: S): S

    abstract operator fun minus(other: S): S

    abstract operator fun times(other: S): S

    abstract operator fun times(scalar: Double): S

    /**
     * Utility method for subclasses to use to check the provided values.
     *
     * @param name the name of the parameter (used only in the error message)
     * @param values the values to check.
     * @throws IllegalArgumentException if the size of the value array does not match the spectral shape.
     */
    protected fun requireSameSize(name: String, values: ImmutableDoubleArray) {
        require(values.size == shape.size) {
            "$name has wrong size (${values.size}) for shape $shape, expected ${shape.size}"
        }
    }

    /**
     * Utility method for subclasses to use to check another spectrum for the same shape.
     *
     * @param other the other spectrum.
     * @throws IllegalArgumentException if the shape of the other spectrum does not match this one.
     */
    protected fun requireSameShape(other: Spectrum<*, *>) {
        if (other.shape != shape) {
            throw IllegalArgumentException("Other spectrum's shape (${other.shape}) does not match ours ($shape)")
        }
    }
}
