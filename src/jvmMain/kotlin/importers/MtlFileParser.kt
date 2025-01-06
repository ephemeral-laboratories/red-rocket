package garden.ephemeral.rocket.importers

import garden.ephemeral.rocket.Material
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.color.Color.Companion.cieXyz
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import kotlinx.io.files.Path

class MtlFileParser(file: Path) {
    val materials = mutableMapOf<String, Material>()
    private var currentMaterialName = ""
    private var currentBuilder = Material.builder()

    init {
        var firstCommand = true
        CommandReader(file).forEachCommand { command ->
            when (command.name) {
                "newmtl" -> {
                    if (command.args.size > 1) {
                        throw InvalidMtlException("Name cannot contain blanks. Got: ${command.args}")
                    }
                    if (!firstCommand) {
                        finishCurrentMaterial()
                    }
                    currentMaterialName = command.args[0]
                    currentBuilder = Material.builder()
                }

                "Ka" -> currentBuilder.ambient = colorFromArgs(command.args)
                "Kd" -> currentBuilder.diffuse = colorFromArgs(command.args)
                "Ks" -> currentBuilder.specular = colorFromArgs(command.args)
                "Ke" -> currentBuilder.emission = colorFromArgs(command.args)
                "Ns" -> currentBuilder.shininess = command.args[0].toDouble()
                "Tf" -> currentBuilder.transparency = colorFromArgs(command.args)
                "Ni" -> currentBuilder.refractiveIndex = command.args[0].toDouble()
                "illum" -> currentBuilder.illuminationModel = command.args[0].toInt()
                "d" -> currentBuilder.dissolve = command.args[0].toDouble()
                else -> {
                    throw UnsupportedMtlException("Unsupported command. Got: $command")
                }
            }
            firstCommand = false
        }
        finishCurrentMaterial()
    }

    private fun colorFromArgs(args: List<String>): Color {
        return when (args[0]) {
            "xyz" -> {
                if (args.size == 2) {
                    val value = args[1].toDouble()
                    cieXyz(value, value, value)
                } else {
                    cieXyz(args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
                }
            }
            else -> {
                if (args[0].toDoubleOrNull() == null) {
                    throw UnsupportedMtlException("Unsupported colour specifier: ${args[0]}")
                }
                if (args.size == 1) {
                    grey(args[0].toDouble())
                } else {
                    linearRgb(args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
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
