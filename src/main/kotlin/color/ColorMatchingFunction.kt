package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.spectra.CieXyzColorSpectrum
import garden.ephemeral.rocket.spectra.SpectralDataFiles
import garden.ephemeral.rocket.spectra.SpectralShape
import garden.ephemeral.rocket.util.Cache

enum class ColorMatchingFunction(val humanName: String, filename: String) {
    CIE_1931_2_DEGREE("CIE 1931 2 Degree Standard Observer", "/color-matching-functions/cie1931_2.spectrum"),
    CIE_1964_10_DEGREE("CIE 1964 10 Degree Standard Observer", "/color-matching-functions/cie1964_10.spectrum"),
    CIE_2012_2_DEGREE("CIE 2012 2 Degree Standard Observer", "/color-matching-functions/cie2012_2.spectrum"),
    CIE_2012_10_DEGREE("CIE 2012 10 Degree Standard Observer", "/color-matching-functions/cie2012_10.spectrum"),
    ;

    private val spectralData by lazy { SpectralDataFiles.readCieXyzColorSpectralData(filename) }

    private val spectrumCache by lazy { Cache<SpectralShape, CieXyzColorSpectrum>() }

    fun spectrum(shape: SpectralShape = SpectralShape.Default): CieXyzColorSpectrum {
        return spectrumCache.get(shape, spectralData::createSpectrum)
    }

    companion object {
        fun forHumanName(humanName: String): ColorMatchingFunction {
            return values().find { i -> i.humanName == humanName }
                ?: throw IllegalArgumentException("Color matching function with name \"$humanName\" not found")
        }
    }
}
