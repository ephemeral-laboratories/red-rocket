package garden.ephemeral.rocket.spectra

object SpectralDataFiles {
    fun readDoubleSpectralData(filename: String): DoubleSpectralData {
        val data = readSpectralData(filename)
        return DoubleSpectralData(data.map { row -> Pair(row[0], row[1]) })
    }

    fun readCieXyzColorSpectralData(filename: String): CieXyzColorSpectralData {
        val data = readSpectralData(filename)
        return CieXyzColorSpectralData(
            DoubleSpectralData(data.map { row -> Pair(row[0], row[1]) }),
            DoubleSpectralData(data.map { row -> Pair(row[0], row[2]) }),
            DoubleSpectralData(data.map { row -> Pair(row[0], row[3]) })
        )
    }

    private fun readSpectralData(filename: String): List<List<Double>> {
        javaClass.getResourceAsStream(filename).use { stream ->
            if (stream == null) {
                throw IllegalArgumentException("No such file: $filename")
            }

            stream.bufferedReader().use { reader ->
                return reader.lineSequence()
                    .map { line -> line.trim() }
                    .filter { line -> line.isNotEmpty() && !line.startsWith('#') }
                    .map { line ->
                        line.split(Regex("\\s+"))
                            .map { cell -> cell.toDouble() }
                    }
                    .toList()
            }
        }
    }
}
