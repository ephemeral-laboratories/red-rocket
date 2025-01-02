package garden.ephemeral.rocket

import io.cucumber.java8.En
import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.writeString

// Constructed reflectively
@Suppress("unused")
class FileStepDefinitions(space: Space) : En {
    private var nextTempDirNumber = 1
    private fun nextTempDirName() = "FileStepDefinitions-temp-$nextTempDirNumber".also { nextTempDirNumber++ }

    private val tempDir: Path by lazy {
        var tempDir: Path
        while (true) {
            tempDir = Path(SystemTemporaryDirectory, nextTempDirName())
            try {
                SystemFileSystem.createDirectories(tempDir, mustCreate = true)
                break
            } catch (e: IOException) {
                // try again
            }
        }
        tempDir.also { dir ->
            After { _ ->
                SystemFileSystem.delete(dir, mustExist = false)
            }
        }
    }

    private fun writeStringToFile(filePath: Path, string: String) {
        SystemFileSystem.sink(filePath).use { rawSink ->
            val sink = rawSink.buffered()
            sink.writeString(string)
            sink.flush()
        }
    }

    init {
        ParameterType("file_var", "file|obj_file|mtl_file|gibberish|ppm") { string -> string }

        Given("{file_var} ← a file containing:") { fv: String, docString: String ->
            space.files[fv] = Path(tempDir, "file.dat").apply {
                writeStringToFile(this, docString)
            }
        }

        Given("{file_var} ← a file named {string} containing:") { fv: String, fileName: String, docString: String ->
            space.files[fv] = Path(tempDir, fileName).apply {
                writeStringToFile(this, docString)
            }
        }

        Given("{file_var} ← the file {string}") { fv: String, string: String ->
            space.files[fv] = Path("src/files/$string")
        }
    }
}
