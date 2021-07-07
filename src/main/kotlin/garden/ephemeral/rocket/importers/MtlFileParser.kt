package garden.ephemeral.rocket.importers

import garden.ephemeral.rocket.Material
import java.io.File

class MtlFileParser(file: File) {
    val materials: MutableMap<String, Material> = mutableMapOf()
    private val whitespace = Regex("\\s+")
    private var currentMaterialName = ""
    private var currentBuilder = Material.Companion.Builder(Material.default)

    init {
        var firstCommand = true
        file.forEachLine line@{ line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                return@line
            }
            val command = trimmedLine.split(whitespace)
            when (command[0]) {
                "newmtl" -> {
                    if (!firstCommand) {
                        finishCurrentMaterial()
                    }
                    currentMaterialName = command[1]
                    currentBuilder = Material.Companion.Builder(Material.default)
                }
            }
            firstCommand = false
        }
        finishCurrentMaterial()
    }

    private fun finishCurrentMaterial() {
        materials[currentMaterialName] = currentBuilder.build()
    }
}