package garden.ephemeral.rocket.spectra

import assertk.assertThat
import assertk.assertions.each
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.CieXyzColor
import garden.ephemeral.rocket.color.ColorStepDefinitions.Companion.colors
import garden.ephemeral.rocket.color.RgbColor
import garden.ephemeral.rocket.color.isCloseTo
import garden.ephemeral.rocket.isCloseTo
import garden.ephemeral.rocket.util.ImmutableDoubleArray
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.toImmutableDoubleArray
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlin.math.abs

class SpectrumStepDefinitions : En {
    private lateinit var spectralShape: SpectralShape
    private lateinit var spectralShape2: SpectralShape
    private lateinit var doubleSpectrum: DoubleSpectrum
    private lateinit var doubleSpectrum1: DoubleSpectrum
    private lateinit var doubleSpectrum2: DoubleSpectrum
    private lateinit var tupleSpectrum: TupleSpectrum
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
            doubleSpectrum = DoubleSpectrum.forBlackBodyRadiation(temperature)
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
                doubleSpectrum = DoubleSpectrum(spectralShape, values)
            } catch (e: IllegalArgumentException) {
                failure = e
            }
        }

        When("an emission spectrum is recovered from the color") {
            doubleSpectrum = DoubleSpectrum.recoverFromCieXyzEmission(colors["color"] as CieXyzColor)
        }

        When("a reflectance spectrum is recovered from the color") {
            doubleSpectrum = DoubleSpectrum.recoverFromCieXyzReflectance(colors["color"] as CieXyzColor)
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

        Then("to_cie_xyz_emission\\(spectrum) = {color}") { expected: CieXyzColor ->
            assertThat(doubleSpectrum.toCieXyzEmission()).isCloseTo(expected, epsilon)
        }

        Then("to_cie_xyz_reflectance\\(spectrum) = {color}") { expected: CieXyzColor ->
            assertThat(doubleSpectrum.toCieXyzReflectance()).isCloseTo(expected, epsilon)
        }

        Then("to_linear_rgb_emission\\(spectrum) = {color}") { expected: RgbColor ->
            assertThat(doubleSpectrum.toLinearRgbEmission()).isCloseTo(expected, epsilon)
        }

        Then("to_linear_rgb_reflectance\\(spectrum) = {color}") { expected: RgbColor ->
            assertThat(doubleSpectrum.toLinearRgbReflectance()).isCloseTo(expected, epsilon)
        }

        Then("spectrum contains no negative values") {
            assertThat(doubleSpectrum.values).each { a -> a.isGreaterThanOrEqualTo(0.0) }
        }
    }

    private fun spectrumFromDataTable(dataTable: DataTable, valueColumn: String = "value"): DoubleSpectrum {
        return DoubleSpectralData(
            dataTable.asMaps()
                .map { row ->
                    Pair(
                        realFromString(row["wavelength"]!!),
                        realFromString(row[valueColumn]!!)
                    )
                }
        ).createSpectrum()
    }

    private fun valuesFromDataTable(dataTable: DataTable): ImmutableDoubleArray = dataTable.asMaps()
        .map { row ->
            realFromString(row["value"]!!)
        }
        .toImmutableDoubleArray()

    private fun spectrumFromTupleDataTable(dataTable: DataTable): TupleSpectrum {
        return TupleSpectrum(
            SpectralShape.Default,
            spectrumFromDataTable(dataTable, "x").values,
            spectrumFromDataTable(dataTable, "y").values,
            spectrumFromDataTable(dataTable, "z").values,
            spectrumFromDataTable(dataTable, "w").values
        )
    }
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
fun DoubleSpectrum.atWavelength(wavelength: Double): Double {
    val index = findIndex(wavelength)
    return values[index]
}
fun TupleSpectrum.atWavelength(wavelength: Double): Tuple {
    val index = findIndex(wavelength)
    return Tuple(xValues[index], yValues[index], zValues[index], wValues[index])
}
fun CieXyzColorSpectrum.atWavelength(wavelength: Double): CieXyzColor {
    val index = findIndex(wavelength)
    return CieXyzColor(xValues[index], yValues[index], zValues[index])
}
private fun Spectrum<*, *>.findIndex(wavelength: Double): Int {
    val index = wavelengths.indexOfFirst { w -> abs(w - wavelength) < epsilon }
    require(index >= 0) { "Wavelength ${wavelength}nm is not in the spectrum" }
    return index
}
