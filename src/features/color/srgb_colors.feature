Feature: sRGB Colors

  Scenario: Linear colors can be converted to sRGB
    Given color ← linear_rgb_color(0.5, 0.4, 0.8)
    Then color_to_srgb_doubles(color) = (0.735356, 0.665185, 0.906331)

  Scenario: sRGB colors can be converted to linear RGB
    Given color ← srgb_color(0.735356, 0.665185, 0.906331)
    Then color.red = 0.5
    And color.green = 0.4
    And color.blue = 0.8