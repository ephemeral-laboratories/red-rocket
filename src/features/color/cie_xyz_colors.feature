Feature: CIE XYZ Colors

  Scenario: XYZ colors are (x, y, z) tuples
    Given color ← cie_xyz_color(0.5, 0.4, 0.8)
    Then color.x = 0.5
    And color.y = 0.4
    And color.z = 0.8

  Scenario: Adding colors
    Given color1 ← cie_xyz_color(0.9, 0.6, 0.75)
    And color2 ← cie_xyz_color(0.7, 0.1, 0.25)
    Then color1 + color2 = cie_xyz_color(1.6, 0.7, 1)

  Scenario: Subtracting colors
    Given color1 ← cie_xyz_color(0.9, 0.6, 0.75)
    And color2 ← cie_xyz_color(0.7, 0.1, 0.25)
    Then color1 - color2 = cie_xyz_color(0.2, 0.5, 0.5)

  Scenario: Multiplying a color by a scalar
    Given color ← cie_xyz_color(0.2, 0.3, 0.4)
    Then color * 2 = cie_xyz_color(0.4, 0.6, 0.8)

  Scenario: Multiplying colors
    Given color1 ← cie_xyz_color(1, 0.2, 0.4)
    And color2 ← cie_xyz_color(0.9, 1, 0.1)
    Then color1 * color2 = cie_xyz_color(0.9, 0.2, 0.04)

  Scenario: CIEXYZ colors can be converted to sRGB
    Given color ← cie_xyz_color(0.5, 0.4, 0.8)
    Then color_to_srgb_doubles(color) = (0.801602, 0.582958, 0.902310)

  Scenario: sRGB colors can be converted to CIEXYZ
    Given color ← srgb_color(0.801602, 0.582958, 0.902310)
    When color2 ← color_to_cie_xyz(color)
    Then color2 = cie_xyz_color(0.5, 0.4, 0.8)

  Scenario: An RGB color plus a CIEXYZ color gives a CIEXYZ color
    Given color1 ← linear_rgb_color(1.0, 0.0, 0.0)
    And color2 ← cie_xyz_color(0.0, 0.0, 1.0)
    Then color1 + color2 = cie_xyz_color(0.412456, 0.212673, 1.019334)
    And color2 + color1 = cie_xyz_color(0.412456, 0.212673, 1.019334)

  Scenario: An RGB color minus a CIEXYZ color gives a CIEXYZ color
    Given color1 ← linear_rgb_color(1.0, 0.0, 0.0)
    And color2 ← cie_xyz_color(0.412456, 0.212673, 1.019334)
    Then color2 - color1 = cie_xyz_color(0, 0, 1)
    # Unrealistic, yes.
    And color1 - color2 = cie_xyz_color(0, 0, -1)

  Scenario: An RGB color times a CIEXYZ color gives a CIEXYZ color
    Given color1 ← linear_rgb_color(1.0, 0.0, 1.0)
    And color2 ← cie_xyz_color(0.0, 0.0, 1.0)
    Then color1 * color2 = cie_xyz_color(0, 0, 0.969638)
    And color2 * color1 = cie_xyz_color(0, 0, 0.969638)