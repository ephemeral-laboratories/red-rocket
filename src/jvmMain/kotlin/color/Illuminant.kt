package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.SpectralDataFiles
import garden.ephemeral.rocket.spectra.SpectralShape

enum class Illuminant(val humanName: String, filename: String) {
    /** [Illuminant A](https://en.wikipedia.org/wiki/Standard_illuminant#Illuminant_A) */
    A("CIE Standard Illuminant A", "/illuminants/a.spectrum"),

    D50("CIE Standard Illuminant D50", "/illuminants/d50.spectrum"),

    /** [Illuminant D65](https://en.wikipedia.org/wiki/Illuminant_D65) */
    D65("CIE Standard Illuminant D65", "/illuminants/d65.spectrum"),

    /** [Illuminant E](https://en.wikipedia.org/wiki/Standard_illuminant#Illuminant_E) */
    E("CIE Standard Illuminant E", "/illuminants/e.spectrum"),

    F1("CIE Standard Illuminant F1", "/illuminants/f1.spectrum"),
    F2("CIE Standard Illuminant F2", "/illuminants/f2.spectrum"),
    F3("CIE Standard Illuminant F3", "/illuminants/f3.spectrum"),
    F4("CIE Standard Illuminant F4", "/illuminants/f4.spectrum"),
    F5("CIE Standard Illuminant F5", "/illuminants/f5.spectrum"),
    F6("CIE Standard Illuminant F6", "/illuminants/f6.spectrum"),
    F7("CIE Standard Illuminant F7", "/illuminants/f7.spectrum"),
    F8("CIE Standard Illuminant F8", "/illuminants/f8.spectrum"),
    F9("CIE Standard Illuminant F9", "/illuminants/f9.spectrum"),
    F10("CIE Standard Illuminant F10", "/illuminants/f10.spectrum"),
    F11("CIE Standard Illuminant F11", "/illuminants/f11.spectrum"),
    F12("CIE Standard Illuminant F12", "/illuminants/f12.spectrum"),

    HP1("CIE Standard Illuminant HP1", "/illuminants/hp1.spectrum"),
    HP2("CIE Standard Illuminant HP2", "/illuminants/hp2.spectrum"),
    HP3("CIE Standard Illuminant HP3", "/illuminants/hp3.spectrum"),
    HP4("CIE Standard Illuminant HP4", "/illuminants/hp4.spectrum"),
    HP5("CIE Standard Illuminant HP5", "/illuminants/hp5.spectrum"),
    ;

    private val spectralData by lazy { SpectralDataFiles.readDoubleSpectralData(filename) }

    fun spectrum(shape: SpectralShape = SpectralShape.Default): DoubleSpectrum {
        return spectralData.createSpectrum(shape)
    }

    companion object {
        fun forHumanName(humanName: String): Illuminant {
            return entries.find { i -> i.humanName == humanName }
                ?: throw IllegalArgumentException("Illuminant with name \"$humanName\" not found")
        }
    }
}
