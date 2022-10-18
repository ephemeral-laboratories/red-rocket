package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.PhysicalConstants
import kotlin.math.PI

/**
 * @property position the position of the light.
 * @property intensity the intensity of the light, in units of radiant intensity (watts per steradian).
 */
data class PointLight private constructor(val position: Tuple, val intensity: DoubleSpectrum) {
    val intensityAsColor by lazy { intensity.toCieXyzEmission() }

    companion object {
        fun build(action: Builder.() -> Unit) = Builder().apply(action).build()
    }

    class Builder {
        private var position: Tuple? = null
        private var spectrum: DoubleSpectrum? = null
        private var radiantIntensity: Double? = null
        private var luminousIntensity: Double? = null

        /**
         * Sets the position of the light.
         *
         * @param position the position of the light.
         */
        fun position(position: Tuple) {
            this.position = position
        }

        /**
         * Sets a spectrum which is used as the shape of the light's intensity spectrum.
         *
         * @param spectrum the spectrum to use. Units are unimportant, only the shape is used.
         */
        fun spectrum(spectrum: DoubleSpectrum) {
            this.spectrum = spectrum
        }

        // TODO: Big problems with how PointLight is defined as a color.
        //       What I think I want:
        //       - Specify physical spectrum
        //       - Specify spectrum and total luminosity
        //       - Specify spectrum and total power
        //       - Specify color and total luminosity
        //       - Specify color and total power
        //       The two color cases collapse to the spectrum cases by recovering a spectrum from the color.
        //       By integrating the spectrum you can get the current power or luminosity and then scale
        //       that to the value which was requested.
        //       We also have to figure out the units!
        fun color(color: Color) {
            spectrum = DoubleSpectrum.recoverFromCieXyzEmission(color.toCieXyz())
        }

        /**
         * Sets the radiant flux desired for the light.
         *
         * @param radiantFlux the radiant flux, in watts.
         */
        fun radiantFlux(radiantFlux: Double) {
            radiantIntensity = radiantFlux / (4 * PI)
            luminousIntensity = null
        }

        /**
         * Sets the radiant intensity desired for the light.
         *
         * @param radiantIntensity the radiant intensity, in watts per steradian.
         */
        fun radiantIntensity(radiantIntensity: Double) {
            this.radiantIntensity = radiantIntensity
            luminousIntensity = null
        }

        /**
         * Sets the luminous flux desired for the light.
         *
         * @param luminousFlux the luminous flux, in lumens.
         */
        fun luminousFlux(luminousFlux: Double) {
            luminousIntensity = luminousFlux / (4 * PI)
            radiantIntensity = null
        }

        /**
         * Sets the luminous intensity desired for the light.
         *
         * @param luminousIntensity the luminous intensity, in candela.
         */
        fun luminousIntensity(luminousIntensity: Double) {
            this.luminousIntensity = luminousIntensity
            radiantIntensity = null
        }

        fun build(): PointLight {
            val position = this.position ?: throw IllegalStateException("position is required")

            // Spectrum for a light would normally be in units of radiant intensity (watts per steradian per nanometre)
            var spectrum = this.spectrum ?: throw IllegalStateException("spectrum is required")

            val radiantIntensity = this.radiantIntensity
            val luminousIntensity = this.luminousIntensity

            // Approach here is to integrate the spectrum to find out what value it has right now,
            // then scale it so that the new spectrum will have the desired total.
            spectrum *= if (radiantIntensity != null) {
                radiantIntensity / spectrum.integrate()
            } else if (luminousIntensity != null) {
                luminousIntensity / (spectrum.integrateLuminosity() * PhysicalConstants.MaximumLuminousEfficacy)
            } else {
                throw IllegalStateException("At least one radiant / luminous flux / intensity property should be set")
            }

            return PointLight(position, spectrum)
        }
    }
}
