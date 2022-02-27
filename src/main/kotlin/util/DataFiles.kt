package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.color.CieXyzColor

object DataFiles {
    fun readDoubleSpectrum(filename: String): Spectrum<Double> {
        return readSpectrum(
            filename,
            { row -> row[0] },
            Spectrum.ValueAdapter.forDouble
        )
    }

    fun readCieXyzColorSpectrum(filename: String): Spectrum<CieXyzColor> {
        return readSpectrum(
            filename,
            { row -> CieXyzColor(row[0], row[1], row[2]) },
            Spectrum.ValueAdapter.forCieXyzColor
        )
    }

    private fun <T> readSpectrum(
        filename: String,
        valueExtractor: (List<Double>) -> T,
        adapter: Spectrum.ValueAdapter<T>
    ): Spectrum<T> {
        javaClass.getResourceAsStream(filename).use { stream ->
            if (stream == null) {
                throw IllegalArgumentException("No such file: $filename")
            }

            stream.bufferedReader().use { reader ->
                val entries = reader.lineSequence()
                    .map { line -> line.trim() }
                    .filter { line -> line.isNotEmpty() && !line.startsWith('#') }
                    .map { line ->
                        val row = line.split(Regex("\\s+"))
                            .map { cell -> cell.toDouble() }

                        val wavelength = row[0]
                        val value = valueExtractor(row.subList(1, row.size))
                        Spectrum.SpectrumEntry(wavelength, value)
                    }
                    .toList()

                return Spectrum(entries, adapter)
            }
        }
    }
}