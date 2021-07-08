package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import garden.ephemeral.rocket.util.RealParser.Companion.realRegex
import io.cucumber.java8.En

class CommonParameterTypes: En {
    init {
        // I can smell this getting much hairier than this, but it would be nice if I could do sweet stuff
        // like (1 + √5)/2 eventually so let's get this started.
        ParameterType("real", realRegex) { string -> realFromString(string) }

        ParameterType("boolean", "true|false") { string -> string == "true" }
    }
}