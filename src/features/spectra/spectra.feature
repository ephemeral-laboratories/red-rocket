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
      | a_min | a_max | a_step | b_min | b_max | b_step | result    |
      | 380   | 780   | 10     | 380   | 780   | 10     | equal     |
      | 380   | 780   | 10     | 380   | 780   | 5      | not equal |
      | 380   | 780   | 10     | 380   | 800   | 10     | not equal |
      | 380   | 780   | 10     | 360   | 780   | 10     | not equal |

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

  Scenario: Converting an emission spectrum to CIE XYZ color
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
    Then to_cie_xyz_emission(spectrum) = cie_xyz_color(573.5369, 479.4662, 63.2889)
    And to_linear_rgb_emission(spectrum) = linear_rgb_color(1089.9627, 346.2040, 1.0007)

  Scenario: Converting a reflectance spectrum to CIE XYZ color
    Given a spectrum with the following data:
      | wavelength | value      |
      | 380        | 0.12142267 |
      | 390        | 0.12142267 |
      | 400        | 0.12142267 |
      | 410        | 0.12142267 |
      | 420        | 0.13415349 |
      | 430        | 0.14107549 |
      | 440        | 0.14455311 |
      | 450        | 0.15570242 |
      | 460        | 0.15793179 |
      | 470        | 0.17319627 |
      | 480        | 0.17625300 |
      | 490        | 0.18248029 |
      | 500        | 0.21415237 |
      | 510        | 0.28556639 |
      | 520        | 0.39043492 |
      | 530        | 0.47046355 |
      | 540        | 0.51299842 |
      | 550        | 0.54612922 |
      | 560        | 0.56384951 |
      | 570        | 0.58202094 |
      | 580        | 0.59808258 |
      | 590        | 0.60983875 |
      | 600        | 0.62561158 |
      | 610        | 0.63450907 |
      | 620        | 0.63898517 |
      | 630        | 0.64850448 |
      | 640        | 0.65801610 |
      | 650        | 0.66075356 |
      | 660        | 0.64988718 |
      | 670        | 0.63223506 |
      | 680        | 0.63322121 |
      | 690        | 0.69249992 |
      | 700        | 0.75293635 |
      | 710        | 0.78131515 |
      | 720        | 0.78940615 |
      | 730        | 0.78927419 |
      | 740        | 0.78497163 |
      | 750        | 0.79003911 |
      | 760        | 0.79641291 |
      | 770        | 0.80388942 |
      | 780        | 0.81081897 |
    Then to_cie_xyz_reflectance(spectrum) = cie_xyz_color(0.49372002, 0.50759728, 0.17809734)
    And to_linear_rgb_reflectance(spectrum) = linear_rgb_color(0.73084267, 0.48111297, 0.11219826)

  Scenario Template: Recovering an emission spectrum from CIE XYZ color
    Given color ← <input>
    When an emission spectrum is recovered from the color
    Then spectrum contains no negative values
    And to_cie_xyz_emission(spectrum) = <input>
    Examples:
      | input                                                         |
      | cie_xyz_color(573.5369, 479.4662, 63.2889)                    |
      | cie_xyz_color(0.000005735369, 0.000004794662, 0.000000632889) |
      | cie_xyz_color(0.0, 0.0, 0.0)                                  |
      | cie_xyz_color(20.0, 20.0, 20.0)                               |
      | cie_xyz_color(0.9642, 1.0000, 0.8249)                         |

  # Pending: Recovering a reflectance spectrum from CIE XYZ color
