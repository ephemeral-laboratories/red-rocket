package garden.ephemeral.rocket.importers

import garden.ephemeral.rocket.Material
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.cieXyz
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import java.nio.file.Path
import kotlin.io.path.forEachLine

class MtlFileParser(file: Path) {
    val materials = mutableMapOf<String, Material>()
    private val whitespace = Regex("\\s+")
    private var currentMaterialName = ""
    private var currentBuilder = Material.builder()

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
                    if (command.size > 2) {
                        throw InvalidMtlException("Name cannot contain blanks. Got: $trimmedLine")
                    }
                    if (!firstCommand) {
                        finishCurrentMaterial()
                    }
                    currentMaterialName = command[1]
                    currentBuilder = Material.builder()
                }
                "Ka" -> currentBuilder.ambient = colorFromCommand(command)
                "Kd" -> currentBuilder.diffuse = colorFromCommand(command)
                "Ks" -> currentBuilder.specular = colorFromCommand(command)
                "Ke" -> currentBuilder.emission = colorFromCommand(command)
                "Ns" -> currentBuilder.shininess = command[1].toDouble()
                "Tf" -> currentBuilder.transparency = colorFromCommand(command)
                "Ni" -> currentBuilder.refractiveIndex = command[1].toDouble()
                "illum" -> currentBuilder.illuminationModel = command[1].toInt()
                "d" -> currentBuilder.dissolve = command[1].toDouble()
                else -> {
                    throw UnsupportedMtlException("Unsupported command. Got: $trimmedLine")
                }
            }
            firstCommand = false
        }
        finishCurrentMaterial()
    }

    private fun colorFromCommand(command: List<String>): Color {
        return when (command[1]) {
            "xyz" -> {
                if (command.size == 3) {
                    val value = command[2].toDouble()
                    cieXyz(value, value, value)
                } else {
                    cieXyz(command[2].toDouble(), command[3].toDouble(), command[4].toDouble())
                }
            }
            else -> {
                if (command[1].toDoubleOrNull() == null) {
                    throw UnsupportedMtlException("Unsupported colour specifier: ${command[1]}")
                }
                if (command.size == 2) {
                    grey(command[1].toDouble())
                } else {
                    linearRgb(command[1].toDouble(), command[2].toDouble(), command[3].toDouble())
                }
            }
        }
    }

    private fun finishCurrentMaterial() {
        materials[currentMaterialName] = currentBuilder.build()
    }

    class InvalidMtlException(message: String, cause: Throwable? = null) :
        IllegalArgumentException(message, cause)

    class UnsupportedMtlException(message: String, cause: Throwable? = null) :
        UnsupportedOperationException(message, cause)
}
