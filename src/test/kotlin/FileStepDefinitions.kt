package garden.ephemeral.rocket

import io.cucumber.java8.En
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.writeText

class FileStepDefinitions: En {
    companion object {
        val files: MutableMap<String, Path> = mutableMapOf()
    }

    private val tempDir: Path by lazy {
        // Safe Path.toFile call because Files.createTempDirectory is guaranteed to produce
        // a path on the default filesystem.
        Files.createTempDirectory("temp").also { t ->
            After { _ ->
                Files.deleteIfExists(t)
            }
        }
    }

    init {
        ParameterType("file_var", "file|obj_file|mtl_file|gibberish|ppm") { string -> string }

        Given("{file_var} ← a file containing:") { fv: String, docString: String ->
            files[fv] = tempDir.resolve("file.dat").apply {
                writeText(docString)
            }
        }

        Given("{file_var} ← a file named {string} containing:") { fv: String, fileName: String, docString: String ->
            files[fv] = tempDir.resolve(fileName).apply {
                writeText(docString)
            }
        }

        Given("{file_var} ← the file {string}") { fv: String, string: String ->
            files[fv] = Path("src/files/$string")
        }
    }
}