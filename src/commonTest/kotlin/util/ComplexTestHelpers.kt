package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex

// RegexOption.COMMENTS is JVM-only, so we're doing some manual collapsing of the pattern here.
private val complexRegex = Regex(
    """
    | \s*
    |   (?:
    |     ($realRegex)
    |     (?:
    |       \s* ([+-]) \s* ((?:$realRegex \s*)? i)
    |     )?
    |   |
    |     (-?(?:$realRegex \s*)? i)
    |   )
    | \s*
    """.trimMargin().replace(Regex("""\s"""), "")
)

fun Complex(value: String): Complex {
    val matchResult = complexRegex.matchEntire(value) ?: throw IllegalArgumentException("Unsupported value: <$value>")
    val (realPartString, joiner, imaginaryPartString1, imaginaryPartString2) = matchResult.destructured

    fun imaginaryPartFromString(string: String): Double {
        if (!string.endsWith("i")) {
            throw IllegalArgumentException("String should have ended with i")
        }
        return when (val front = string.substring(0, string.length - 1).trim()) {
            "" -> 1.0
            "-" -> -1.0
            else -> realFromString(front)
        }
    }

    return if (realPartString.isNotEmpty()) {
        if (imaginaryPartString1.isNotEmpty()) {
            val imaginarySign = if (joiner == "-") -1.0 else 1.0
            Complex(
                realFromString(realPartString),
                imaginarySign * imaginaryPartFromString(imaginaryPartString1)
            )
        } else {
            Complex(realFromString(realPartString), 0.0)
        }
    } else if (imaginaryPartString2.isNotEmpty()) {
        Complex(0.0, imaginaryPartFromString(imaginaryPartString2))
    } else {
        throw AssertionError("It shouldn't have matched the regex")
    }
}
