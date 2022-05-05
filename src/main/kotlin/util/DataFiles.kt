package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.color.CieXyzColor

object DataFiles {
    fun readDoubleSpectralData(filename: String): SpectralData<Double> {
        return readSpectralData(
            filename,
            { row -> row[0] },
            Spectrum.ValueAdapter.forDouble
        )
    }

    fun readCieXyzColorSpectralData(filename: String): SpectralData<CieXyzColor> {
        return readSpectralData(
            filename,
            { row -> CieXyzColor(row[0], row[1], row[2]) },
            Spectrum.ValueAdapter.forCieXyzColor
        )
    }

    private fun <T> readSpectralData(
        filename: String,
        valueExtractor: (List<Double>) -> T,
        adapter: Spectrum.ValueAdapter<T>
    ): SpectralData<T> {
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
                        Pair(wavelength, value)
                    }
                    .toList()

                return SpectralData(entries, adapter)
            }
        }
    }
}
