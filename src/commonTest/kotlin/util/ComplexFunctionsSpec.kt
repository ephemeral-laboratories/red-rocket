package garden.ephemeral.rocket.util

import io.kotest.core.spec.style.FreeSpec

class ComplexFunctionsSpec : FreeSpec({
    "Complex Functions" - {
        "Square root" - {
            "for complex" - {
                withStringTable2<Complex, Complex>(
                    """
                    | value   | expected root |
                    | 0       | 0             |
                    | 1       | 1             |
                    | -1      | i             |
                    | 81      | 9             |
                    | -4      | 2i            |
                    | i       | 1/√2 + 1/√2i  |
                    | 2i      | 1 + i         |
                    | -3 + 4i | 1 + 2i        |
                    """.trimIndent()
                ) { (value, expectedRoot) ->
                    sqrt(value) shouldBeCloseTo expectedRoot
                }
            }

            "for real" - {
                withStringTable2<Double, Complex>(
                    """
                    | value   | expected root |
                    | 0       | 0             |
                    | 1       | 1             |
                    | -1      | i             |
                    | 81      | 9             |
                    | -4      | 2i            |
                    """.trimIndent()
                ) { (value, expectedRoot) ->
                    complexSqrt(value) shouldBeCloseTo expectedRoot
                }
            }
        }

        "Sine" - {
            withStringTable2<Complex, Complex>(
                """
                | value      | expected sine            |
                | 0          | 0                        |
                | π          | 0                        |
                | π/2        | 1                        |
                | -π/2       | -1                       |
                | π/2i       | 2.3012989i               |
                | 0.3 + 0.4i | 0.31947873 + 0.39240669i |
                """.trimIndent()
            ) { (value, expectedSine) ->
                sin(value) shouldBeCloseTo expectedSine
            }
        }

        "Cosine" - {
            withStringTable2<Complex, Complex>(
                """
                | value      | expected cosine          |
                | 0          | 1                        |
                | π          | -1                       |
                | π/2        | 0                        |
                | -π/2       | 0                        |
                | π/2i       | 2.50917847866            |
                | 0.3 + 0.4i | 1.03278788 - 0.12138561i |
                """.trimIndent()
            ) { (value, expectedCosine) ->
                cos(value) shouldBeCloseTo expectedCosine
            }
        }

        "Tangent" - {
            withStringTable2<Complex, Complex>(
                """
                | value      | expected tangent         |
                | 0          | 0                        |
                | π          | 0                        |
                # These cases _basically_ pass, they produce giant numbers which are practically infinity,
                # but I will have to think about the correct behaviour of `isCloseTo` when the result is
                # infinity. :(
                #| π/2        | ∞                        |
                #| -π/2       | -∞                       |
                | π/2i       | 0.91715233i              |
                | 0.3 + 0.4i | 0.26107368 + 0.41063348i |
                """.trimIndent()
            ) { (value, expectedTangent) ->
                tan(value) shouldBeCloseTo expectedTangent
            }
        }

        // XXX: We don't have exp(z) to match our ln(z), because we never needed to do the operation.
        //      We do have a method to convert from polar coordinates, which is kind of filling that role?

        "Natural logarithm" - {
            withStringTable2<Complex, Complex>(
                """
                | value | expected logarithm |
                | 0     | -∞                 |
                | 1     | 0                  |
                | -1    | πi                 |
                | i     | π/2 i              |
                | -i    | -π/2 i             |
                | 1 + i | 0.346573 + π/4 i   |
                """.trimIndent()
            ) { (value, expectedLogarithm) ->
                ln(value) shouldBeCloseTo expectedLogarithm
            }
        }

        "Arcsine" - {
            withStringTable2<Complex, Complex>(
                """
                | value                    | expected arcsine |
                | 0                        | 0                |
                | 1                        | π/2              |
                | -1                       | -π/2             |
                | 2.3012989i               | π/2i             |
                | 0.31947873 + 0.39240669i | 0.3 + 0.4i       |
                """.trimIndent()
            ) { (value, expectedArcsine) ->
                arcsin(value) shouldBeCloseTo expectedArcsine
            }
        }

        "Arccosine" - {
            withStringTable2<Complex, Complex>(
                """
                | value                    | expected arccosine |
                | 1                        | 0                  |
                | -1                       | π                  |
                | 0                        | π/2                |
                | 2.50917847866            | π/2i               |
                | 1.03278788 - 0.12138561i | 0.3 + 0.4i         |
                """.trimIndent()
            ) { (value, expectedArccosine) ->
                arccos(value) shouldBeCloseTo expectedArccosine
            }
        }

        "Arctangent" - {
            withStringTable2<Complex, Complex>(
                """
                | value                    | expected arctangent |
                | 0                        | 0                   |
                | 0.91715233i              | π/2i                |
                | 0.26107368 + 0.41063348i | 0.3 + 0.4i          |
                """.trimIndent()
            ) { (value, expectedArctangent) ->
                arctan(value) shouldBeCloseTo expectedArctangent
            }
        }
    }
})
