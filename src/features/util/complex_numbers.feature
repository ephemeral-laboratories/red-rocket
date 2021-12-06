Feature: Complex Numbers

  Scenario Outline: Formatting a complex number
    When z ← complex(<value>)
    Then z.to_string = 'complex(<expected string>)'

    Examples:
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

  Scenario Outline: Adding two complex numbers
    When z1 ← complex(<augend>)
    And z2 ← complex(<addend>)
    Then z1 + z2 = complex(<expected total>)

    Examples:
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

  Scenario Outline: Adding a complex number and a real number
    When z ← complex(<augend>)
    Then z + <addend> = complex(<expected total>)
    And <addend> + z = complex(<expected total>)

    Examples:
      | augend | addend | expected total  |
      | 1      | 2      | 3               |
      | i      | 2      | 2 + i           |
      | 3 + 2i | 3      | 6 + 2i          |

  Scenario Outline: Subtracting two complex numbers
    When z1 ← complex(<minuend>)
    And z2 ← complex(<subtrahend>)
    Then z1 - z2 = complex(<expected difference>)

    Examples:
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

  Scenario Outline: Subtracting a real number from a complex number
    When z ← complex(<minuend>)
    Then z - <subtrahend> = complex(<expected difference>)

    Examples:
      | minuend | subtrahend | expected difference |
      | 3       | 1          | 2                   |
      | i       | 2          | -2 + i              |
      | 3 + 2i  | 3          | 2i                  |

  Scenario Outline: Subtracting a complex number from a real number
    When z ← complex(<subtrahend>)
    Then <minuend> - z = complex(<expected difference>)

    Examples:
      | subtrahend | minuend | expected difference |
      | 3          | 1       | -2                  |
      | i          | 2       | 2 - i               |
      | 3 + 2i     | 3       | -2i                 |

  Scenario Outline: Multiplying two complex numbers
    When z1 ← complex(<multiplicand>)
    And z2 ← complex(<multiplier>)
    Then z1 * z2 = complex(<expected product>)

    Examples:
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

  Scenario Outline: Multiplying a complex number by a real number
    When z ← complex(<multiplicand>)
    Then z * <multiplier> = complex(<expected product>)
    And <multiplier> * z = complex(<expected product>)

    Examples:
      | multiplicand | multiplier | expected product |
      | 0            | 1          | 0                |
      | 0            | 2          | 0                |
      | 1            | 0          | 0                |
      | -2           | 3          | -6               |
      | i            | 0          | 0                |
      | 3i           | 2          | 6i               |

  Scenario Outline: Finding the conjugate of a complex number
    When z ← complex(<value>)
    When z.conjugate = complex(<expected conjugate>)

    Examples:
    | value   | expected conjugate |
    | 0       | 0                  |
    | 1       | 1                  |
    | -2      | -2                 |
    | i       | -i                 |
    | -3i     | 3i                 |
    | 3 + 2i  | 3 - 2i             |
    | -4 - 3i | -4 + 3i            |

  Scenario Outline: Dividing two complex numbers
    When z1 ← complex(<dividend>)
    And z2 ← complex(<divisor>)
    Then z1 / z2 = complex(<expected quotient>)

    Examples:
      | dividend | divisor | expected quotient |
      | 4        | 2       | 2                 |
      | 6i       | 2       | 3i                |
      | -6i      | 3i      | -2                |
      | 2 + 3i   | 4       | 0.5 + 0.75i       |
      | 20 - 4i  | 3 + 2i  | 4 - 4i            |
      | 4 + 2i   | -1 + i  | -1 - 3i           |

  Scenario Outline: Dividing a complex number by a real number
    When z ← complex(<dividend>)
    Then z / <divisor> = complex(<expected quotient>)

    Examples:
      | dividend | divisor | expected quotient |
      | 4        | 2       | 2                 |
      | 6i       | 2       | 3i                |
      | 2 + 3i   | 4       | 0.5 + 0.75i       |

  Scenario Outline: Dividing a real number by a complex number
    When z ← complex(<divisor>)
    Then <dividend> / z = complex(<expected quotient>)

    Examples:
      | dividend | divisor | expected quotient        |
      | 2        | 4       | 0.5                      |
      | 2        | 6i      | -1/3i                    |
      | 4        | 2 + 3i  | 0.61538461 - 0.92307692i |

  # TODO: Sensible example for dividing by 0, choose whether it's an error or Infinity

  Scenario Outline: Converting a complex number to polar coordinates
    When z ← complex(<value>)
    Then z.magnitude = <expected magnitude>
    And z.argument = <expected argument>

    Examples:
      | value        | expected magnitude | expected argument |
      | 0            | 0                  | NaN               |
      | 1            | 1                  | 0                 |
      | 2            | 2                  | 0                 |
      | i            | 1                  | π/2               |
      | -2i          | 2                  | -π/2              |
      | 1/√2 + 1/√2i | 1                  | π/4               |
      | 3 + 2i       | 3.60555127         | 0.58800260        |

  Scenario Outline: Creating a complex number from polar coordinates
    When z ← complex_from_polar(<magnitude>, <argument>)
    Then z = complex(<expected value>)

    Examples:
      | magnitude  | argument   | expected value |
      | 0          | 0          | 0              |
      | 1          | 0          | 1              |
      | 2          | 0          | 2              |
      | 1          | π/2        | i              |
      | 2          | -π/2       | -2i            |
      | 1          | π/4        | 1/√2 + 1/√2i   |
      | 3.60555127 | 0.58800260 | 3 + 2i         |

  Scenario Outline: Taking a complex number to a real power
    When z ← complex(<root>)
    Then z^<power> = complex(<expected result>)

    Examples:
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

  Scenario Outline: Squaring a complex number
    When z ← complex(<root>)
    Then z^2 = complex(<expected result>)

    Examples:
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

  Scenario Outline: Taking the square root of a complex number
    When z ← complex(<value>)
    Then sqrt(z) = complex(<expected root>)

    Examples:
      | value   | expected root |
      | 0       | 0             |
      | 1       | 1             |
      | -1      | i             |
      | 81      | 9             |
      | -4      | 2i            |
      | i       | 1/√2 + 1/√2i  |
      | 2i      | 1 + i         |
      | -3 + 4i | 1 + 2i        |

  Scenario Outline: Taking the square root of a real number
    When z ← complex(<expected root>)
    Then sqrt(<value>) = z

    Examples:
      | value   | expected root |
      | 0       | 0             |
      | 1       | 1             |
      | -1      | i             |
      | 81      | 9             |
      | -4      | 2i            |
