package garden.ephemeral.rocket.util

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.RgbColor
import garden.ephemeral.rocket.color.isCloseTo
import garden.ephemeral.rocket.isCloseTo
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.Spectrum.Companion.div
import garden.ephemeral.rocket.util.Spectrum.Companion.toCieXyz
import garden.ephemeral.rocket.util.Spectrum.Companion.toLinearRgb
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlin.math.abs

class SpectrumStepDefinitions : En {
    lateinit var spectralShape: SpectralShape
    lateinit var spectralShape2: SpectralShape
    lateinit var doubleSpectrum: Spectrum<Double>
    lateinit var doubleSpectrum1: Spectrum<Double>
    lateinit var doubleSpectrum2: Spectrum<Double>
    lateinit var tupleSpectrum: Spectrum<Tuple>
    var failure: IllegalArgumentException? = null

    init {
        Given("the default spectral shape") {
            spectralShape = SpectralShape.Default
        }
        Given("a spectral shape from {real}nm to {real}nm in steps of {real}nm") { min: Double, max: Double, step: Double ->
            spectralShape = SpectralShape(min, max, step)
        }
        Given("another spectral shape from {real}nm to {real}nm in steps of {real}nm") { min: Double, max: Double, step: Double ->
            spectralShape2 = SpectralShape(min, max, step)
        }

        Given("a spectrum with the following data:") { dataTable: DataTable ->
            doubleSpectrum = spectrumFromDataTable(dataTable)
        }

        Given("one spectrum with the following data:") { dataTable: DataTable ->
            doubleSpectrum1 = spectrumFromDataTable(dataTable)
        }

        Given("another spectrum with the following data:") { dataTable: DataTable ->
            doubleSpectrum2 = spectrumFromDataTable(dataTable)
        }

        Given("a spectrum with the following tuple data:") { dataTable: DataTable ->
            tupleSpectrum = spectrumFromTupleDataTable(dataTable)
        }

        Given("the spectrum of black body radiation at {real} Kelvin") { temperature: Double ->
            doubleSpectrum = Spectrum.forBlackBodyRadiation(temperature)
        }

        When("the two spectra are added") {
            doubleSpectrum = doubleSpectrum1 + doubleSpectrum2
        }
        When("the two spectra are multiplied") {
            doubleSpectrum = doubleSpectrum1 * doubleSpectrum2
        }
        When("the first spectrum is divided by the second spectrum") {
            doubleSpectrum = doubleSpectrum1 / doubleSpectrum2
        }

        When("trying to create a spectrum with the following values:") { dataTable: DataTable ->
            try {
                val values = valuesFromDataTable(dataTable)
                doubleSpectrum = Spectrum(spectralShape, values, Spectrum.ValueAdapter.forDouble)
            } catch (e: IllegalArgumentException) {
                failure = e
            }
        }

        Then("the spectral shapes are equal") {
            assertThat(spectralShape).isEqualTo(spectralShape2)
        }
        Then("the spectral shapes are not equal") {
            assertThat(spectralShape).isNotEqualTo(spectralShape2)
        }

        Then("spectral_shape.min = {real}") { expected: Double ->
            assertThat(spectralShape.min).isCloseTo(expected, epsilon)
        }
        Then("spectral_shape.max = {real}") { expected: Double ->
            assertThat(spectralShape.max).isCloseTo(expected, epsilon)
        }
        Then("spectral_shape.step = {real}") { expected: Double ->
            assertThat(spectralShape.step).isCloseTo(expected, epsilon)
        }
        Then("spectral_shape.size = {int}") { expected: Int ->
            assertThat(spectralShape.size).isEqualTo(expected)
        }

        Then("spectrum[{real}] = {real}") { wavelength: Double, expected: Double ->
            assertThat(doubleSpectrum.atWavelength(wavelength)).isCloseTo(expected, epsilon)
        }
        Then("spectrum[{real}] = {tuple}") { wavelength: Double, expected: Tuple ->
            assertThat(tupleSpectrum.atWavelength(wavelength)).isCloseTo(expected, epsilon)
        }

        Then("spectrum contains data:") { dataTable: DataTable ->
            val expectedSpectrum = spectrumFromDataTable(dataTable)
            assertThat(doubleSpectrum.shape).isEqualTo(expectedSpectrum.shape)
            for (index in expectedSpectrum.values.indices) {
                assertThat(doubleSpectrum.values[index]).isCloseTo(expectedSpectrum.values[index], epsilon)
            }
        }

        Then("creation of the spectrum fails") {
            assertThat(failure).isNotNull()
        }

        Then("to_cie_xyz\\(spectrum\\) = {color}") { expected: CieXyzColor ->
            assertThat(doubleSpectrum.toCieXyz()).isCloseTo(expected, epsilon)
        }

        Then("to_linear_rgb\\(spectrum\\) = {color}") { expected: RgbColor ->
            assertThat(doubleSpectrum.toLinearRgb()).isCloseTo(expected, epsilon)
        }
    }

    private fun spectrumFromDataTable(dataTable: DataTable): Spectrum<Double> = SpectralData(
        dataTable.asMaps()
            .map { row ->
                Pair(
                    realFromString(row["wavelength"]!!),
                    realFromString(row["value"]!!)
                )
            },
        Spectrum.ValueAdapter.forDouble
    ).createSpectrum()

    private fun valuesFromDataTable(dataTable: DataTable): List<Double> = dataTable.asMaps()
        .map { row ->
            realFromString(row["value"]!!)
        }

    private fun spectrumFromTupleDataTable(dataTable: DataTable): Spectrum<Tuple> = SpectralData(
        dataTable.asMaps()
            .map { row ->
                Pair(
                    realFromString(row["wavelength"]!!),
                    Tuple(
                        realFromString(row["x"]!!),
                        realFromString(row["y"]!!),
                        realFromString(row["z"]!!),
                        realFromString(row["w"]!!)
                    )
                )
            },
        Spectrum.ValueAdapter.forTuple
    ).createSpectrum()
}

/**
 * Gets the value of a spectrum at the given wavelength.
 *
 * This is in test code because performance would be bad. It isn't the kind of operation
 * production code will ever need to do.
 *
 * The spectrum's spectral shape must contain the given wavelength -
 * no interpolation of values is performed.
 *
 * @param wavelength the wavelength to look up.
 * @return the spectrum value at that wavelength.
 * @throws IllegalArgumentException if the wavelength is not in the spectrum.
 */
fun <T> Spectrum<T>.atWavelength(wavelength: Double): T {
    val index = wavelengths.indexOfFirst { w -> abs(w - wavelength) < epsilon }
    require (index >= 0) { "Wavelength ${wavelength}nm is not in the spectrum" }
    return values[index]
}
