package garden.ephemeral.rocket.importers

/**
 * Immutable container class for a single command.
 *
 * @property lineNumber the line number the command started at.
 * @property args the arguments to the command.
 */
data class Command(val lineNumber: Int, val name: String, val args: List<String>)
