package garden.ephemeral.rocket.util

import kotlinx.io.Source
import kotlinx.io.readLine

fun Source.forEachLine(action: (line: String) -> Unit) {
    while (true) {
        val line = this.readLine() ?: break
        action(line)
    }
}
