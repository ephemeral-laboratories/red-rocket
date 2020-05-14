package org.trypticon.rocket

import io.cucumber.java8.En
import kotlin.Double.Companion.POSITIVE_INFINITY

class CommonParameterTypes: En {
    companion object {
        private const val doubleTermRegex: String = "\\d+(?:\\.\\d+)?"
        private const val realTermRegex: String = "-?√?(?:$doubleTermRegex|π|infinity)"
        const val realRegex: String = "$realTermRegex(?:\\s*\\/\\s*$realTermRegex)?"

        private fun realTermFromString(string: String): Double {
            var s = string
            var negative = false
            var root = false
            if (s.startsWith("-")) {
                s = s.substring(1)
                negative = true
            }
            if (s.startsWith("√")) {
                s = s.substring(1)
                root = true
            }

            var n = when (s) {
                "π" -> { Math.PI }
                "infinity" -> { POSITIVE_INFINITY }
                else -> { s.toDouble() }
            }

            if (root) {
                n = Math.sqrt(n)
            }
            if (negative) {
                n = -n
            }
            return n
        }

        fun realFromString(string: String): Double {
            // How long until we need a proper parser here just to parse numbers?
            if (string.contains("/")) {
                val rational = string.split("/")
                return realTermFromString(
                    rational[0].trim()
                ) / realTermFromString(rational[1].trim())
            } else {
                return realTermFromString(string)
            }
        }
    }

    init {
        // I can smell this getting much hairier than this, but it would be nice if I could do sweet stuff
        // like (1 + √5)/2 eventually so let's get this started.
        ParameterType("real", realRegex) { string -> realFromString(string) }

        ParameterType("boolean", "true|false") { string -> string == "true" }
    }
}