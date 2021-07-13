Feature: Colors

  Scenario: Colors are (red, green, blue) tuples
    Given color ← color(-0.5, 0.4, 1.7)
    Then color.red = -0.5
    And color.green = 0.4
    And color.blue = 1.7

  Scenario: Adding colors
    Given color1 ← color(0.9, 0.6, 0.75)
    And color2 ← color(0.7, 0.1, 0.25)
    Then color1 + color2 = color(1.6, 0.7, 1)

  Scenario: Subtracting colors
    Given color1 ← color(0.9, 0.6, 0.75)
    And color2 ← color(0.7, 0.1, 0.25)
    Then color1 - color2 = color(0.2, 0.5, 0.5)

  Scenario: Multiplying a color by a scalar
    Given color ← color(0.2, 0.3, 0.4)
    Then color * 2 = color(0.4, 0.6, 0.8)

  Scenario: Multiplying colors
    Given color1 ← color(1, 0.2, 0.4)
    And color2 ← color(0.9, 1, 0.1)
    Then color1 * color2 = color(0.9, 0.2, 0.04)
