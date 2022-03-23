Feature: Spectra

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

  Scenario: Illegally creating a spectrum with no data
    When trying to create a spectrum with the following data:
      | wavelength | value |
    Then creation of the spectrum fails

  Scenario: Illegally creating a spectrum with only one datum
    When trying to create a spectrum with the following data:
      | wavelength | value |
      | 400        | 1     |
    Then creation of the spectrum fails

  Scenario: Illegally creating a spectrum with data out of order
    When trying to create a spectrum with the following data:
      | wavelength | value |
      | 400        | 1     |
      | 300        | 2     |
    Then creation of the spectrum fails

  Scenario: Linear interpolation of values
    Given a spectrum with the following data:
      | wavelength | value |
      | 400        | 1     |
      | 500        | 2     |
    Then spectrum[450] = 1.5
    And spectrum[475] = 1.75

  Scenario: Linear extrapolation of high values
    Given a spectrum with the following data:
      | wavelength | value |
      | 400        | 1     |
      | 500        | 2     |
    Then spectrum[600] = 3
    And spectrum[550] = 2.5

  Scenario: Linear extrapolation of low values
    Given a spectrum with the following data:
      | wavelength | value |
      | 400        | 2     |
      | 500        | 3     |
    Then spectrum[300] = 1
    And spectrum[350] = 1.5

  Scenario: Creating a spectrum with tuple values
    Given a spectrum with the following tuple data:
      | wavelength | x | y | z | w |
      | 400        | 1 | 2 | 3 | 4 |
      | 700        | 5 | 6 | 7 | 8 |
    Then spectrum[550] = tuple(3, 4, 5, 6)

  Scenario: Adding two spectra
    Given one spectrum with the following data:
      | wavelength | value |
      | 400        | 4     |
      | 600        | 6     |
    And another spectrum with the following data:
      | wavelength | value |
      | 500        | 5     |
      | 700        | 7     |
    When the two spectra are added
    Then spectrum[400] = 8
    And spectrum[600] = 12
    And spectrum[700] = 14

  Scenario: Multiplying two spectra
    Given one spectrum with the following data:
      | wavelength | value |
      | 400        | 4     |
      | 600        | 6     |
    And another spectrum with the following data:
      | wavelength | value |
      | 500        | 5     |
      | 700        | 7     |
    When the two spectra are multiplied
    Then spectrum[400] = 16
    And spectrum[600] = 36

  Scenario: Black body radiation
    Given the spectrum of black body radiation at 5778 Kelvin
    Then spectrum[380] = 67436936395798.0
    And spectrum[500] = 82861471747495.05
    And spectrum[780] = 55508985788916.53

  Scenario: Converting a spectrum to CIE XYZ color
    Given the spectrum of black body radiation at 5778 Kelvin
    Then to_cie_xyz(spectrum) = cie_xyz_color(0.32644607014662996, 0.33575798782133853, 0.3377959420320315)

  Scenario: Converting a spectrum to linear RGB color
    Given the spectrum of black body radiation at 5778 Kelvin
    Then to_linear_rgb(spectrum) = linear_rgb_color(0.37332512542188356, 0.3275099828794392, 0.30678762598616227)
