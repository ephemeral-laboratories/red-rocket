Feature: Color Matching Functions

  Scenario: CIE 1931 2 Degree Standard Observer
    Given Color matching function "CIE 1931 2 Degree Standard Observer"
    Then cmf.spectrum[400] = cie_xyz_color(0.014310000000, 0.000396000000, 0.067850010000)

  Scenario: CIE 1964 10 Degree Standard Observer
    Given Color matching function "CIE 1964 10 Degree Standard Observer"
    Then cmf.spectrum[400] = cie_xyz_color(0.019109700000, 0.002004400000, 0.086010900000)

  Scenario: CIE 2012 2 Degree Standard Observer
    Given Color matching function "CIE 2012 2 Degree Standard Observer"
    Then cmf.spectrum[400] = cie_xyz_color(0.02214302, 0.002452194, 0.1096090)

  Scenario: CIE 2012 10 Degree Standard Observer
    Given Color matching function "CIE 2012 10 Degree Standard Observer"
    Then cmf.spectrum[400] = cie_xyz_color(0.01879338, 0.002589775, 0.08508254)
