Feature: Complex Numbers

  Scenario Outline: Formatting a complex number
    When z is <value>
    Then z.to_string = '<expected string>'

    Examples:
      | value           | expected string      |
      | complex(0)      | complex(0.0)         |
      | complex(1)      | complex(1.0)         |
      | complex(-1)     | complex(-1.0)        |
      | complex(i)      | complex(1.0i)        |
      | complex(-i)     | complex(-1.0i)       |
      | complex(2i)     | complex(2.0i)        |
      | complex(-2i)    | complex(-2.0i)       |
      | complex(1 + i)  | complex(1.0 + 1.0i)  |
      | complex(3 + 2i) | complex(3.0 + 2.0i)  |
      | complex(-2 - i) | complex(-2.0 - 1.0i) |

  Scenario Outline: Adding two complex numbers
    When z1 is <augend>
    And z2 is <addend>
    Then z1 + z2 = <expected total>

    Examples:
      | augend          | addend          | expected total  |
      | complex(0)      | complex(1)      | complex(1)      |
      | complex(0)      | complex(2)      | complex(2)      |
      | complex(1)      | complex(0)      | complex(1)      |
      | complex(-2)     | complex(3)      | complex(1)      |
      | complex(0)      | complex(i)      | complex(1i)     |
      | complex(0)      | complex(2i)     | complex(2i)     |
      | complex(i)      | complex(0)      | complex(i)      |
      | complex(2i)     | complex(-3i)    | complex(-i)     |
      | complex(3 + 2i) | complex(3 + i)  | complex(6 + 3i) |

  Scenario Outline: Subtracting two complex numbers
    When z1 is <minuend>
    And z2 is <subtrahend>
    Then z1 - z2 = <expected difference>

    Examples:
      | minuend         | subtrahend      | expected difference |
      | complex(0)      | complex(1)      | complex(-1)         |
      | complex(0)      | complex(2)      | complex(-2)         |
      | complex(1)      | complex(0)      | complex(1)          |
      | complex(-2)     | complex(3)      | complex(-5)         |
      | complex(0)      | complex(i)      | complex(-i)         |
      | complex(0)      | complex(2i)     | complex(-2i)        |
      | complex(i)      | complex(0)      | complex(i)          |
      | complex(2i)     | complex(-3i)    | complex(5i)         |
      | complex(3 + 2i) | complex(3 + i)  | complex(i)          |

  Scenario Outline: Multiplying two complex numbers
    When z1 is <multiplicand>
    And z2 is <multiplier>
    Then z1 * z2 = <expected product>

    Examples:
      | multiplicand    | multiplier      | expected product |
      | complex(0)      | complex(1)      | complex(0)       |
      | complex(0)      | complex(2)      | complex(0)       |
      | complex(1)      | complex(0)      | complex(0)       |
      | complex(-2)     | complex(3)      | complex(-6)      |
      | complex(0)      | complex(i)      | complex(0)       |
      | complex(0)      | complex(2i)     | complex(0)       |
      | complex(i)      | complex(i)      | complex(-1)      |
      | complex(i)      | complex(0)      | complex(0)       |
      | complex(1)      | complex(2i)     | complex(2i)      |
      | complex(2i)     | complex(-3i)    | complex(6)       |
      | complex(3 + 2i) | complex(3 + i)  | complex(7 + 9i)  |

  Scenario Outline: Finding the conjugate of a complex number
    When z is <value>
    When z.conjugate = <expected conjugate>

    Examples:
    | value            | expected conjugate |
    | complex(0)       | complex(0)         |
    | complex(1)       | complex(1)         |
    | complex(-2)      | complex(-2)        |
    | complex(i)       | complex(-i)        |
    | complex(-3i)     | complex(3i)        |
    | complex(3 + 2i)  | complex(3 - 2i)    |
    | complex(-4 - 3i) | complex(-4 + 3i)   |

  Scenario Outline: Dividing two complex numbers
    When z1 is <dividend>
    And z2 is <divisor>
    Then z1 / z2 = <expected quotient>

    Examples:
      | dividend         | divisor         | expected quotient    |
      | complex(4)       | complex(2)      | complex(2)           |
      | complex(6i)      | complex(2)      | complex(3i)          |
      | complex(-6i)     | complex(3i)     | complex(-2)          |
      | complex(2 + 3i)  | complex(4)      | complex(0.5 + 0.75i) |
      | complex(20 - 4i) | complex(3 + 2i) | complex(4 - 4i)      |
      | complex(4 + 2i)  | complex(-1 + i) | complex(-1 - 3i)     |

  # TODO: Sensible example for dividing by 0, choose whether it's an error or Infinity
