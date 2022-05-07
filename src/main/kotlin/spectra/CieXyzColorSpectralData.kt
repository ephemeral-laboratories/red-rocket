package garden.ephemeral.rocket.spectra

class CieXyzColorSpectralData(
    val x: DoubleSpectralData,
    val y: DoubleSpectralData,
    val z: DoubleSpectralData
) : SpectralData<CieXyzColorSpectrum>() {
    override fun createSpectrum(shape: SpectralShape): CieXyzColorSpectrum = CieXyzColorSpectrum(
        shape,
        x.createSpectrum(shape).values,
        y.createSpectrum(shape).values,
        z.createSpectrum(shape).values
    )
}