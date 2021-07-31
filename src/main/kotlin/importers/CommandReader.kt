package garden.ephemeral.rocket.importers

import java.nio.file.Path
import kotlin.io.path.forEachLine

/**
 * Utility to read OBJ style commands from a plain-text file.
 *
 * @param file the file.
 */
internal class CommandReader(private val file: Path) {
    private val whitespace = Regex("\\s+")

    /**
     * Opens the file, reads it line by line, interpreting it as commands and
     * passing commands to the provided action.
     *
     * @param action the action to perform for each command.
     */
    fun forEachCommand(action: (Command) -> Unit) {
        var lineNumber = 0
        var partialLine: String? = null
        file.forEachLine { line ->
            lineNumber++

            // Line coalescing logic
            val commandLine = if (partialLine == null) {
                line
            } else {
                partialLine + line
            }
            if (commandLine.endsWith("\\")) {
                partialLine = commandLine.substring(0, commandLine.length - 1)
                return@forEachLine
            } else {
                partialLine = null
            }

            val trimmed = commandLine.trim()

            // Skip comments
            if (trimmed.startsWith("#")) {
                return@forEachLine
            }

            val values = trimmed.split(whitespace)

            val command = Command(lineNumber, values[0], values.subList(1, values.size))
            action.invoke(command)
        }
    }
}