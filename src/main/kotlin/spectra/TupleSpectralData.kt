package garden.ephemeral.rocket.spectra

class TupleSpectralData(
    val x: DoubleSpectralData,
    val y: DoubleSpectralData,
    val z: DoubleSpectralData,
    val w: DoubleSpectralData
) : SpectralData<TupleSpectrum>() {
    override fun createSpectrum(shape: SpectralShape): TupleSpectrum = TupleSpectrum(
        shape,
        x.createSpectrum(shape).values,
        y.createSpectrum(shape).values,
        z.createSpectrum(shape).values,
        w.createSpectrum(shape).values
    )
}