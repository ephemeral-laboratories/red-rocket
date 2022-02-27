package garden.ephemeral.rocket.color

import garden.ephemeral.rocket.util.DataFiles

enum class Illuminant(filename: String) {
    A("/illuminants/a.spectrum"),
    D50("/illuminants/d50.spectrum"),
    D65("/illuminants/d65.spectrum"),
    F2("/illuminants/f2.spectrum"),
    ;

    val spectrum by lazy { DataFiles.readDoubleSpectrum(filename) }
}
