package garden.ephemeral.rocket

import io.cucumber.java8.En
import java.io.File
import java.nio.file.Files

class FileStepDefinitions: En {
    companion object {
        val files: MutableMap<String, File> = mutableMapOf()
    }

    init {
        ParameterType("file_var", "file|gibberish|ppm") { string -> string }

        Given("{file_var} ← a file containing:") { fv: String, docString: String ->
            val file = File.createTempFile("scenario", ".obj")
            After { _ ->
                Files.deleteIfExists(file.toPath())
            }
            files[fv] = file.apply {
                writeText(docString)
            }
        }
        Given("{file_var} ← the file {string}") { fv: String, string: String ->
            files[fv] = File("src/files/$string")
        }
    }
}