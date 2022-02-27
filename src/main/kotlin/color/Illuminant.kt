package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.util.DataFiles

enum class Illuminant(val humanName: String, filename: String) {
    A("CIE Standard Illuminant A", "/illuminants/a.spectrum"),
    D50("CIE Standard Illuminant D50", "/illuminants/d50.spectrum"),
    D65("CIE Standard Illuminant D65", "/illuminants/d65.spectrum"),
    F2("CIE Standard Illuminant F2", "/illuminants/f2.spectrum"),
    ;

    val spectrum by lazy { DataFiles.readDoubleSpectrum(filename) }

    companion object {
        fun forHumanName(humanName: String): Illuminant {
            return values().find { i -> i.humanName == humanName }
                ?: throw IllegalArgumentException("Illuminant with name \"$humanName\" not found")
        }
    }
}
