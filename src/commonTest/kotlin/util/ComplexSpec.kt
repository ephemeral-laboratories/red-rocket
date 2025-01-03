package garden.ephemeral.rocket.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ComplexSpec : FreeSpec({
    "Complex Numbers" - {

        "Formatting a complex number" - {
            withStringTable2<Complex, String>(
                """
                | value  | expected string |
                | 0      | 0.0             |
                | 1      | 1.0             |
                | -1     | -1.0            |
                | i      | 1.0i            |
                | -i     | -1.0i           |
                | 2i     | 2.0i            |
                | -2i    | -2.0i           |
                | 1 + i  | 1.0 + 1.0i      |
                | 3 + 2i | 3.0 + 2.0i      |
                | -2 - i | -2.0 - 1.0i     |
                """.trimIndent(),
            ) { (value, expectedString) ->
                value.toString() shouldBe "complex($expectedString)"
            }
        }

        "Addition and subtraction" - {
            "Adding two complex numbers" - {
                withStringTable3<Complex, Complex, Complex>(
                    """
                | augend | addend | expected total  |
                | 0      | 1      | 1               |
                | 0      | 2      | 2               |
                | 1      | 0      | 1               |
                | -2     | 3      | 1               |
                | 0      | i      | 1i              |
                | 0      | 2i     | 2i              |
                | i      | 0      | i               |
                | 2i     | -3i    | -i              |
                | 3 + 2i | 3 + i  | 6 + 3i          |
                """.trimIndent()
                ) { (augend, addend, expectedTotal) ->
                    (augend + addend) shouldBeCloseTo expectedTotal
                    (addend + augend) shouldBeCloseTo expectedTotal
                }
            }

            "Adding a complex number and a real number" - {
                withStringTable3<Complex, Double, Complex>(
                    """
                | augend | addend | expected total  |
                | 1      | 2      | 3               |
                | i      | 2      | 2 + i           |
                | 3 + 2i | 3      | 6 + 2i          |
                """.trimIndent()
                ) { (augend, addend, expectedTotal) ->
                    (augend + addend) shouldBeCloseTo expectedTotal
                    (addend + augend) shouldBeCloseTo expectedTotal
                }
            }

            "Subtracting two complex numbers" - {
                withStringTable3<Complex, Complex, Complex>(
                    """
                | minuend | subtrahend | expected difference |
                | 0       | 1          | -1                  |
                | 0       | 2          | -2                  |
                | 1       | 0          | 1                   |
                | -2      | 3          | -5                  |
                | 0       | i          | -i                  |
                | 0       | 2i         | -2i                 |
                | i       | 0          | i                   |
                | 2i      | -3i        | 5i                  |
                | 3 + 2i  | 3 + i      | i                   |
                """.trimIndent()
                ) { (minuend, subtrahend, expectedDifference) ->
                    (minuend - subtrahend) shouldBeCloseTo expectedDifference
                }
            }

            "Subtracting a real number from a complex number" - {
                withStringTable3<Complex, Double, Complex>(
                    """
                | minuend | subtrahend | expected difference |
                | 3       | 1          | 2                   |
                | i       | 2          | -2 + i              |
                | 3 + 2i  | 3          | 2i                  |
                """.trimIndent()
                ) { (minuend, subtrahend, expectedDifference) ->
                    (minuend - subtrahend) shouldBeCloseTo expectedDifference
                }
            }

            "Subtracting a complex number from a real number" - {
                withStringTable3<Complex, Double, Complex>(
                    """
                | subtrahend | minuend | expected difference |
                | 3          | 1       | -2                  |
                | i          | 2       | 2 - i               |
                | 3 + 2i     | 3       | -2i                 |
                """.trimIndent()
                ) { (subtrahend, minuend, expectedDifference) ->
                    (minuend - subtrahend) shouldBeCloseTo expectedDifference
                }
            }
        }

        "Multiplication and division" - {
            "Multiplying two complex numbers" - {
                withStringTable3<Complex, Complex, Complex>(
                    """
                | multiplicand | multiplier | expected product |
                | 0            | 1          | 0                |
                | 0            | 2          | 0                |
                | 1            | 0          | 0                |
                | -2           | 3          | -6               |
                | 0            | i          | 0                |
                | 0            | 2i         | 0                |
                | i            | i          | -1               |
                | i            | 0          | 0                |
                | 1            | 2i         | 2i               |
                | 2i           | -3i        | 6                |
                | 3 + 2i       | 3 + i      | 7 + 9i           |
                """.trimIndent()
                ) { (multiplicand, multiplier, expectedProduct) ->
                    (multiplicand * multiplier) shouldBeCloseTo expectedProduct
                    (multiplier * multiplicand) shouldBeCloseTo expectedProduct
                }
            }

            "Multiplying a complex number by a real number" - {
                withStringTable3<Complex, Double, Complex>(
                    """
                | multiplicand | multiplier | expected product |
                | 0            | 1          | 0                |
                | 0            | 2          | 0                |
                | 1            | 0          | 0                |
                | -2           | 3          | -6               |
                | i            | 0          | 0                |
                | 3i           | 2          | 6i               |
                """.trimIndent()
                ) { (multiplicand, multiplier, expectedProduct) ->
                    (multiplicand * multiplier) shouldBeCloseTo expectedProduct
                    (multiplier * multiplicand) shouldBeCloseTo expectedProduct
                }
            }

            "Dividing two complex numbers" - {
                withStringTable3<Complex, Complex, Complex>(
                    """
                | dividend | divisor | expected quotient |
                | 4        | 2       | 2                 |
                | 6i       | 2       | 3i                |
                | -6i      | 3i      | -2                |
                | 2 + 3i   | 4       | 0.5 + 0.75i       |
                | 20 - 4i  | 3 + 2i  | 4 - 4i            |
                | 4 + 2i   | -1 + i  | -1 - 3i           |
                """.trimIndent()
                ) { (dividend, divisor, expectedQuotient) ->
                    (dividend / divisor) shouldBeCloseTo expectedQuotient
                }
            }

            "Dividing a complex number by a real number" - {
                withStringTable3<Complex, Double, Complex>(
                    """
                | dividend | divisor | expected quotient |
                | 4        | 2       | 2                 |
                | 6i       | 2       | 3i                |
                | 2 + 3i   | 4       | 0.5 + 0.75i       |
                """.trimIndent()
                ) { (dividend, divisor, expectedQuotient) ->
                    (dividend / divisor) shouldBeCloseTo expectedQuotient
                }
            }

            "Dividing a real number by a complex number" - {
                withStringTable3<Double, Complex, Complex>(
                    """
                | dividend | divisor | expected quotient        |
                | 2        | 4       | 0.5                      |
                | 2        | 6i      | -1/3i                    |
                | 4        | 2 + 3i  | 0.61538461 - 0.92307692i |
                """.trimIndent()
                ) { (dividend, divisor, expectedQuotient) ->
                    (dividend / divisor) shouldBeCloseTo expectedQuotient
                }
            }

            // TODO: Sensible example for dividing by 0, choose whether it's an error or Infinity
        }

        "Finding the conjugate of a complex number" - {
            withStringTable2<Complex, Complex>(
                """
                | value   | expected conjugate |
                | 0       | 0                  |
                | 1       | 1                  |
                | -2      | -2                 |
                | i       | -i                 |
                | -3i     | 3i                 |
                | 3 + 2i  | 3 - 2i             |
                | -4 - 3i | -4 + 3i            |
                """.trimIndent()
            ) { (value, expectedConjugate) ->
                value.conjugate shouldBeCloseTo expectedConjugate
            }
        }

        "Converting a complex number to polar coordinates" - {
            withStringTable3<Complex, Double, Double>(
                """
                | value        | expected magnitude | expected argument |
                | 0            | 0                  | 0                 |
                | 1            | 1                  | 0                 |
                | 2            | 2                  | 0                 |
                | i            | 1                  | π/2               |
                | -2i          | 2                  | -π/2              |
                | 1/√2 + 1/√2i | 1                  | π/4               |
                | 3 + 2i       | 3.60555127         | 0.58800260        |
                """.trimIndent()
            ) { (value, expectedMagnitude, expectedArgument) ->
                value.magnitude shouldBeCloseTo expectedMagnitude
                value.argument shouldBeCloseTo expectedArgument.rad
            }
        }

        "Creating a complex number from polar coordinates" - {
            withStringTable3<Double, Double, Complex>(
                """
                | magnitude  | argument   | expected value |
                | 0          | 0          | 0              |
                | 1          | 0          | 1              |
                | 2          | 0          | 2              |
                | 1          | π/2        | i              |
                | 2          | -π/2       | -2i            |
                | 1          | π/4        | 1/√2 + 1/√2i   |
                | 3.60555127 | 0.58800260 | 3 + 2i         |
                """.trimIndent()
            ) { (magnitude, argument, expectedValue) ->
                Complex.fromPolar(magnitude, argument.rad) shouldBeCloseTo expectedValue
            }
        }

        "Taking a complex number to a real power" - {
            withStringTable3<Complex, Double, Complex>(
                """
                | root         | power | expected result |
                # Technically 0^0 is undefined but calculators tend to say 1 so I am being pragmatic
                | 0            | 0     | 1               |
                | 10           | 0     | 1               |
                | 10           | 1     | 10              |
                | -1           | 2     | 1               |
                | 1            | 9999  | 1               |
                | 10           | 2     | 100             |
                | 49           | 0.5   | 7               |
                | 0.5          | 2     | 0.25            |
                | 10           | -1    | 0.1             |
                | -1           | 0.5   | i               |
                | i            | 2     | -1              |
                | 2i           | 2     | -4              |
                | -2i          | 2     | -4              |
                | i            | 0.5   | 1/√2 + 1/√2i    |
                | 1/√2 + 1/√2i | 2     | i               |
                | 3 + 2i       | 5     | -597 + 122i     |
                """.trimIndent()
            ) { (root, power, expectedResult) ->
                (root pow power) shouldBeCloseTo expectedResult
            }
        }

        "Squaring a complex number" - {
            withStringTable2<Complex, Complex>(
                """
                | root         | expected result |
                | 0            | 0               |
                | -1           | 1               |
                | 10           | 100             |
                | 0.5          | 0.25            |
                | i            | -1              |
                | 2i           | -4              |
                | -2i          | -4              |
                | 1/√2 + 1/√2i | i               |
                | 1 + i        | 2i              |
                | 1 + 2i       | -3 + 4i         |
                """.trimIndent()
            ) { (root, expectedResult) ->
                root.squared() shouldBeCloseTo expectedResult
            }
        }
    }
})
