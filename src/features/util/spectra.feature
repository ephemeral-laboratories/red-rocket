Feature: Spectra

  Scenario: Creating a spectrum
    Given a spectrum with the following data:
      | wavelength | value |
      | 400        | 4     |
      | 500        | 5     |
      | 600        | 6     |
      | 700        | 7     |
    Then spectrum[400] = 4
    And spectrum[600] = 6

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
