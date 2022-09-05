package garden.ephemeral.rocket

import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum

data class PointLight(val position: Tuple, val intensity: DoubleSpectrum) {
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
    constructor(position: Tuple, intensity: Color) : this(
        position,
        DoubleSpectrum.recoverFromCieXyzEmission(intensity.toCieXyz())
    )

    val intensityAsColor by lazy { intensity.toCieXyzEmission() }
}
