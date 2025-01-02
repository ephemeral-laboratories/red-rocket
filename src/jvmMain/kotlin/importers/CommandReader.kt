package garden.ephemeral.rocket.importers

import garden.ephemeral.rocket.util.forEachLine
import kotlinx.io.Source

/**
 * Utility to read OBJ style commands from a plain-text file.
 *
 * @param source the source to read from.
 */
internal class CommandReader(private val source: Source) {
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

        source.forEachLine { line ->
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
