Feature: Illuminants

  Scenario: Standard Illuminant A
    Given Standard Illuminant A
    Then illuminant.spectrum[400] = 14.708

  Scenario: Standard Illuminant D50
    Given Standard Illuminant D50
    Then illuminant.spectrum[400] = 49.308

  Scenario: Standard Illuminant D65
    Given Standard Illuminant D65
    Then illuminant.spectrum[400] = 82.7549

  Scenario: Standard Illuminant F2
    Given Standard Illuminant F2
    Then illuminant.spectrum[400] = 3.44
