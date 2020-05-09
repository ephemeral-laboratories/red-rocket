import io.cucumber.java8.En

class CommonParameterTypes: En {
    companion object {
        val realRegex : String = "-?√?\\d+\\.\\d+(?:\\/-?√?\\d+\\.\\d+)?"

        fun realFromString(string: String) : Double {

            // How long until we need a proper parser here just to parse numbers?
            if (string.contains("/")) {
                val rational = string.split("/")
                return realFromString(rational[0]) / realFromString(rational[1])
            }

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
            var n = s.toDouble()
            if (root) {
                n = Math.sqrt(n)
            }
            if (negative) {
                n = -n
            }
            return n
        }
    }

    init {
        ParameterType("var", "[a-z][A-Za-z0-9_]*") { string -> string }
        ParameterType("mvar", "[A-Z][A-Za-z0-9_]*") { string -> string }

        // I can smell this getting much hairier than this, but it would be nice if I could do sweet stuff
        // like (1 + √5)/2 eventually so let's get this started.
        ParameterType("real", realRegex) { string -> realFromString(string) }
    }
}