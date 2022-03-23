Feature: Illuminants

  Scenario Outline: <illuminant>
    Given <illuminant>
    Then illuminant.spectrum[400] = <value_for_400nm>
    Examples:
      | illuminant                  | value_for_400nm |
      | CIE Standard Illuminant A   | 14.708          |
      | CIE Standard Illuminant D50 | 49.308          |
      | CIE Standard Illuminant D65 | 82.7549         |
      | CIE Standard Illuminant E   | 100.0           |
      | CIE Standard Illuminant F1  | 5.17            |
      | CIE Standard Illuminant F2  | 3.44            |
      | CIE Standard Illuminant F3  | 2.57            |
      | CIE Standard Illuminant F4  | 2.01            |
      | CIE Standard Illuminant F5  | 5.1             |
      | CIE Standard Illuminant F6  | 3.11            |
      | CIE Standard Illuminant F7  | 6.15            |
      | CIE Standard Illuminant F8  | 3.17            |
      | CIE Standard Illuminant F9  | 2.59            |
      | CIE Standard Illuminant F10 | 1.48            |
      | CIE Standard Illuminant F11 | 1.29            |
      | CIE Standard Illuminant F12 | 1.19            |
