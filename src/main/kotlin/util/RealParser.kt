package garden.ephemeral.rocket.util

class RealParser {
    companion object {
        private const val doubleTermRegex = "\\d+(?:\\.\\d+)?"
        private const val realTermRegex = "(?:-?(?:√?(?:$doubleTermRegex|π)|∞))"
        const val realRegex = "(?:$realTermRegex(?:\\s*\\/\\s*$realTermRegex)?)"

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
                "∞" -> {
                    Double.POSITIVE_INFINITY
                }
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
    }
}