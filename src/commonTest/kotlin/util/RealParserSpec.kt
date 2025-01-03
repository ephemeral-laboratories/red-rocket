package garden.ephemeral.rocket.util

import io.kotest.core.spec.style.FreeSpec

class RealParserSpec : FreeSpec({
    "Real Parser" - {
        "Parses various values thrown at us in Jamis' examples" - {
            withStringTable2<String, Double>(
                """
                | string | expected    |
                |  0     |  0.0        |
                |  1     |  1.0        |
                |  0.25  |  0.25       |
                | -1.3   | -1.3        |
                |  1/2   |  0.5        |
                | -1/2   | -0.5        |
                |  1/3   |  0.33333333 |
                |  3/2   |  1.5        |
                | √2     |  1.41421356 |
                | √2/2   |  0.70710678 |
                |  π     |  3.14159265 |
                | 2π     |  6.28318531 |
                | -π     | -3.14159265 |
                |  π/2   |  1.57079632 |
                | -π/2   | -1.57079632 |
                |  ∞     |  Infinity   |
                | -∞     | -Infinity   |
                | √(π/6) |  0.72360125 |
                """.trimIndent()
            ) { (string, expected) ->
                RealParser.realFromString(string) shouldBeCloseTo expected
            }
        }
    }
})