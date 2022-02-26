package garden.ephemeral.rocket.util

import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isNotNull
import garden.ephemeral.rocket.Constants.Companion.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.isCloseTo
import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class SpectrumStepDefinitions : En {
    lateinit var doubleSpectrum: Spectrum<Double>
    lateinit var doubleSpectrum1: Spectrum<Double>
    lateinit var doubleSpectrum2: Spectrum<Double>
    lateinit var tupleSpectrum: Spectrum<Tuple>
    var failure: IllegalArgumentException? = null

    init {
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

        When("the two spectra are added") {
            doubleSpectrum = doubleSpectrum1 + doubleSpectrum2
        }

        When("trying to create a spectrum with the following data:") { dataTable: DataTable ->
            try {
                doubleSpectrum = spectrumFromDataTable(dataTable)
            } catch (e: IllegalArgumentException) {
                failure = e
            }
        }

        Then("spectrum[{real}] = {real}") { wavelength: Double, expectedValue: Double ->
            assertThat(doubleSpectrum[wavelength]).isCloseTo(expectedValue, epsilon)
        }

        Then("spectrum[{real}] = {tuple}") { wavelength: Double, expectedValue: Tuple ->
            assertThat(tupleSpectrum[wavelength]).isCloseTo(expectedValue, epsilon)
        }

        Then("creation of the spectrum fails") {
            assertThat(failure).isNotNull()
        }
    }

    private fun spectrumFromDataTable(dataTable: DataTable): Spectrum<Double> = Spectrum(
        dataTable.asMaps()
            .map { row ->
                Spectrum.SpectrumEntry(
                    realFromString(row["wavelength"]!!),
                    realFromString(row["value"]!!)
                )
            },
        Spectrum.ValueAdapter.forDouble
    )

    private fun spectrumFromTupleDataTable(dataTable: DataTable): Spectrum<Tuple> = Spectrum(
        dataTable.asMaps()
            .map { row ->
                Spectrum.SpectrumEntry(
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
    )
}