Feature: Spectra

  Scenario: Creating the default spectral shape
    Given the default spectral shape
    Then spectral_shape.min = 380
    And spectral_shape.max = 780
    And spectral_shape.step = 10
    And spectral_shape.size = 41

  Scenario: Creating an arbitrary spectral shape
    Given a spectral shape from 380nm to 780nm in steps of 20nm
    Then spectral_shape.size = 21

  Scenario Outline: Comparing two spectral shapes
    Given a spectral shape from <a_min>nm to <a_max>nm in steps of <a_step>nm
    And another spectral shape from <b_min>nm to <b_max>nm in steps of <b_step>nm
    Then the spectral shapes are <result>
    Examples:
      | a_min | a_max | a_step | b_min | b_max | b_step | result     |
      | 380   | 780   | 10     | 380   | 780   | 10     | equal      |
      | 380   | 780   | 10     | 380   | 780   | 5      | not equal  |
      | 380   | 780   | 10     | 380   | 800   | 10     | not equal  |
      | 380   | 780   | 10     | 360   | 780   | 10     | not equal  |

  Scenario: Creating a spectrum
    Given a spectrum with the following data:
      | wavelength | value |
      | 400        | 4     |
      | 500        | 5     |
      | 600        | 6     |
      | 700        | 7     |
    Then spectrum contains data:
      | wavelength | value |
      | 400        | 4     |
      | 500        | 5     |
      | 600        | 6     |
      | 700        | 7     |

  Scenario: Illegally creating a spectrum with the wrong number of values
    Given the default spectral shape
    When trying to create a spectrum with the following values:
      | wavelength | value |
      | 400        | 1     |
      | 500        | 2     |
      | 600        | 3     |
      | 700        | 4     |
    Then creation of the spectrum fails

  Scenario: Creating a spectrum with tuple values
    Given a spectrum with the following tuple data:
      | wavelength | x | y | z | w |
      | 400        | 1 | 2 | 3 | 4 |
      | 700        | 5 | 6 | 7 | 8 |
    Then spectrum[400] = tuple(1, 2, 3, 4)
    And spectrum[700] = tuple(5, 6, 7, 8)

  Scenario: Adding two spectra
    Given one spectrum with the following data:
      | wavelength | value |
      | 400        | 4     |
      | 600        | 6     |
    And another spectrum with the following data:
      | wavelength | value |
      | 400        | 5     |
      | 600        | 7     |
    When the two spectra are added
    Then spectrum[400] = 9
    And spectrum[600] = 13

  Scenario: Multiplying two spectra
    Given one spectrum with the following data:
      | wavelength | value |
      | 400        | 4     |
      | 600        | 6     |
    And another spectrum with the following data:
      | wavelength | value |
      | 400        | 0.4   |
      | 600        | 0.6   |
    When the two spectra are multiplied
    Then spectrum[400] = 1.6
    And spectrum[600] = 3.6

  Scenario: Dividing two spectra
    Given one spectrum with the following data:
      | wavelength | value |
      | 400        | 4     |
      | 600        | 6     |
    And another spectrum with the following data:
      | wavelength | value |
      | 400        | 0.4   |
      | 600        | 0.6   |
    When the first spectrum is divided by the second spectrum
    Then spectrum[400] = 10
    And spectrum[600] = 10

  Scenario: Black body radiation
    Given the spectrum of black body radiation at 5778 Kelvin
    Then spectrum[380] = 67436936395798.0
    And spectrum[500] = 82861471747495.05
    And spectrum[780] = 55508985788916.53

  Scenario: Converting a spectrum to CIE XYZ color
    Given a spectrum with the following data:
      | wavelength | value    |
      | 380        | 0.00019  |
      | 390        | 0.00025  |
      | 400        | 0.00031  |
      | 410        | 0.00038  |
      | 420        | 0.00048  |
      | 430        | 0.000589 |
      | 440        | 0.000789 |
      | 450        | 0.001289 |
      | 460        | 0.00043  |
      | 470        | 0.001299 |
      | 480        | 0.00014  |
      | 490        | 0.00032  |
      | 500        | 0.005624 |
      | 510        | 0.00021  |
      | 520        | 0.00021  |
      | 530        | 0.00022  |
      | 540        | 0.00026  |
      | 550        | 0.001139 |
      | 560        | 0.002078 |
      | 570        | 0.025403 |
      | 580        | 0.011178 |
      | 590        | 0.014255 |
      | 600        | 0.01894  |
      | 610        | 0.007992 |
      | 620        | 0.004685 |
      | 630        | 0.003247 |
      | 640        | 0.002537 |
      | 650        | 0.002038 |
      | 660        | 0.001778 |
      | 670        | 0.001918 |
      | 680        | 0.001369 |
      | 690        | 0.000819 |
      | 700        | 0.000699 |
      | 710        | 0.000649 |
      | 720        | 0.000609 |
      | 730        | 0.000579 |
      | 740        | 0.000579 |
      | 750        | 0.000639 |
      | 760        | 0.000559 |
      | 770        | 0.002787 |
      | 780        | 0.000669 |
    Then to_cie_xyz(spectrum) = cie_xyz_color(573.5369, 479.4662, 63.2889)
    And to_linear_rgb(spectrum) = linear_rgb_color(1089.9627, 346.2040, 1.0007)
