package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.color.CieXyzColor

object DataFiles {
    fun readCieColorSpectrum(filename: String): Spectrum<CieXyzColor> {
        javaClass.getResourceAsStream(filename).use { stream ->
            if (stream == null) {
                throw IllegalArgumentException("No such file: $filename")
            }

            stream.bufferedReader().use { reader ->
                val entries = reader.lineSequence()
                    .map { line -> line.trim() }
                    .filter { line -> !line.startsWith('#') }
                    .map { line ->
                        val row = line.split(Regex("\\s"))
                        val wavelength = row[0].toDouble()
                        val x = row[1].toDouble()
                        val y = row[2].toDouble()
                        val z = row[3].toDouble()
                        Spectrum.SpectrumEntry(wavelength, CieXyzColor(x, y, z))
                    }
                    .toList()
                return Spectrum(entries, Spectrum.ValueAdapter.forCieXyzColor)
            }
        }
    }
}