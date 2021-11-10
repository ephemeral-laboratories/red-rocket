Feature: Mueller Matrices

  # Creation scenarios

  Scenario: Mueller matrix for a linear polarizer (horizontal transmission)
    When M is a Mueller matrix for a linear polarizer (horizontal transmission)
    Then M is the following 4x4 matrix:
      | 0.5 | 1/2 | 0 | 0 |
      | 1/2 | 1/2 | 0 | 0 |
      | 0   | 0   | 0 | 0 |
      | 0   | 0   | 0 | 0 |

  Scenario: Mueller matrix for a linear polarizer (vertical transmission)
    When M is a Mueller matrix for a linear polarizer (vertical transmission)
    Then M is the following 4x4 matrix:
      |  1/2 | -1/2 | 0 | 0 |
      | -1/2 |  1/2 | 0 | 0 |
      |  0   |  0   | 0 | 0 |
      |  0   |  0   | 0 | 0 |

  Scenario: Mueller matrix for a linear polarizer (+45° transmission)
    When M is a Mueller matrix for a linear polarizer (+45° transmission)
    Then M is the following 4x4 matrix:
      | 1/2 | 0 | 1/2 | 0 |
      | 0   | 0 | 0   | 0 |
      | 1/2 | 0 | 1/2 | 0 |
      | 0   | 0 | 0   | 0 |

  Scenario: Mueller matrix for a linear polarizer (-45° transmission)
    When M is a Mueller matrix for a linear polarizer (-45° transmission)
    Then M is the following 4x4 matrix:
      |  1/2 | 0 | -1/2 | 0 |
      |  0   | 0 |  0   | 0 |
      | -1/2 | 0 |  1/2 | 0 |
      |  0   | 0 |  0   | 0 |

  Scenario: Mueller matrix for a quarter-wave plate (fast-axis vertical)
    When M is a Mueller matrix for a quarter-wave plate (fast-axis vertical)
    Then M is the following 4x4 matrix:
      | 1 | 0 | 0 |  0 |
      | 0 | 1 | 0 |  0 |
      | 0 | 0 | 0 | -1 |
      | 0 | 0 | 1 |  0 |

  Scenario: Mueller matrix for a quarter-wave plate (fast-axis horizontal)
    When M is a Mueller matrix for a quarter-wave plate (fast-axis horizontal)
    Then M is the following 4x4 matrix:
      | 1 | 0 |  0 | 0 |
      | 0 | 1 |  0 | 0 |
      | 0 | 0 |  0 | 1 |
      | 0 | 0 | -1 | 0 |

  # (fast-axis horizontal and vertical; also, ideal mirror)
  Scenario: Mueller matrix for a half-wave plate
    When M is a Mueller matrix for a half-wave plate
    Then M is the following 4x4 matrix:
      | 1 | 0 |  0 |  0 |
      | 0 | 1 |  0 |  0 |
      | 0 | 0 | -1 |  0 |
      | 0 | 0 |  0 | -1 |

  Scenario: Mueller matrix for a reference frame rotation of 45 degrees
    When M is a Mueller matrix for a reference rotation of 45 degrees
    Then M is the following 4x4 matrix:
      | 1 |  0 | 0 | 0 |
      | 0 |  0 | 1 | 0 |
      | 0 | -1 | 0 | 0 |
      | 0 |  0 | 0 | 1 |

  Scenario: Mueller matrix for a reference frame rotation of 30 degrees
    When M is a Mueller matrix for a reference rotation of 30 degrees
    Then M is the following 4x4 matrix:
      | 1 |  0        | 0        | 0 |
      | 0 |  0.5      | 0.866025 | 0 |
      | 0 | -0.866025 | 0.5      | 0 |
      | 0 |  0        | 0        | 1 |

  # Note: Identical to quarter-wave plate (fast-axis vertical) to check that consistency
  Scenario: Mueller matrix for a linear retarder with fast axis 0 degrees and phase difference 90 degrees
    When M is a Mueller matrix for a linear retarder with fast axis 0 degrees and phase difference 90 degrees
    Then M is the following 4x4 matrix:
      | 1 | 0 | 0 |  0 |
      | 0 | 1 | 0 |  0 |
      | 0 | 0 | 0 | -1 |
      | 0 | 0 | 1 |  0 |

  Scenario: Mueller matrix for a linear retarder with fast axis 30 degrees and phase difference 60 degrees
    When M is a Mueller matrix for a linear retarder with fast axis 30 degrees and phase difference 60 degrees
    Then M is the following 4x4 matrix:
      | 1 | 0        | 0        |  0        |
      | 0 | 0.625    | 0.216506 |  0.75     |
      | 0 | 0.216506 | 0.875    | -0.433012 |
      | 0 | -0.75    | 0.433012 |  0.5      |

  # Calculus examples

  Scenario Outline: <incoming light> hitting a <type of filter>
    Given S is a Stokes vector for <incoming light> light
    And S2 is a Stokes vector for <outgoing light> light
    And S2 ← S2 * <intensity>
    When S passes through a <type of filter>
    Then S = S2

    Examples:
      | incoming light                    | type of filter                             | outgoing light                    | intensity |
      | unpolarized                       | linear polarizer (horizontal transmission) | linearly polarized (horizontal)   | 1/2       |
      | unpolarized                       | linear polarizer (vertical transmission)   | linearly polarized (vertical)     | 1/2       |
      | unpolarized                       | linear polarizer (+45° transmission)       | linearly polarized (+45°)         | 1/2       |
      | unpolarized                       | linear polarizer (-45° transmission)       | linearly polarized (-45°)         | 1/2       |
      | unpolarized                       | quarter-wave plate (fast-axis vertical)    | unpolarized                       | 1         |
      | unpolarized                       | quarter-wave plate (fast-axis horizontal)  | unpolarized                       | 1         |
      | unpolarized                       | half-wave plate                            | unpolarized                       | 1         |
      | linearly polarized (horizontal)   | linear polarizer (horizontal transmission) | linearly polarized (horizontal)   | 1         |
      | linearly polarized (horizontal)   | linear polarizer (vertical transmission)   | no                                | 0         |
      | linearly polarized (horizontal)   | linear polarizer (+45° transmission)       | linearly polarized (+45°)         | 1/2       |
      | linearly polarized (horizontal)   | linear polarizer (-45° transmission)       | linearly polarized (-45°)         | 1/2       |
      | linearly polarized (horizontal)   | quarter-wave plate (fast-axis vertical)    | linearly polarized (horizontal)   | 1         |
      | linearly polarized (horizontal)   | quarter-wave plate (fast-axis horizontal)  | linearly polarized (horizontal)   | 1         |
      | linearly polarized (horizontal)   | half-wave plate                            | linearly polarized (horizontal)   | 1         |
      | linearly polarized (vertical)     | linear polarizer (horizontal transmission) | no                                | 0         |
      | linearly polarized (vertical)     | linear polarizer (vertical transmission)   | linearly polarized (vertical)     | 1         |
      | linearly polarized (vertical)     | linear polarizer (+45° transmission)       | linearly polarized (+45°)         | 1/2       |
      | linearly polarized (vertical)     | linear polarizer (-45° transmission)       | linearly polarized (-45°)         | 1/2       |
      | linearly polarized (vertical)     | quarter-wave plate (fast-axis vertical)    | linearly polarized (vertical)     | 1         |
      | linearly polarized (vertical)     | quarter-wave plate (fast-axis horizontal)  | linearly polarized (vertical)     | 1         |
      | linearly polarized (vertical)     | half-wave plate                            | linearly polarized (vertical)     | 1         |
      | linearly polarized (+45°)         | linear polarizer (horizontal transmission) | linearly polarized (horizontal)   | 1/2       |
      | linearly polarized (+45°)         | linear polarizer (vertical transmission)   | linearly polarized (vertical)     | 1/2       |
      | linearly polarized (+45°)         | linear polarizer (+45° transmission)       | linearly polarized (+45°)         | 1         |
      | linearly polarized (+45°)         | linear polarizer (-45° transmission)       | no                                | 0         |
      | linearly polarized (+45°)         | quarter-wave plate (fast-axis vertical)    | circularly polarized (right hand) | 1         |
      | linearly polarized (+45°)         | quarter-wave plate (fast-axis horizontal)  | circularly polarized (left hand)  | 1         |
      | linearly polarized (+45°)         | half-wave plate                            | linearly polarized (-45°)         | 1         |
      | linearly polarized (-45°)         | linear polarizer (horizontal transmission) | linearly polarized (horizontal)   | 1/2       |
      | linearly polarized (-45°)         | linear polarizer (vertical transmission)   | linearly polarized (vertical)     | 1/2       |
      | linearly polarized (-45°)         | linear polarizer (+45° transmission)       | no                                | 0         |
      | linearly polarized (-45°)         | linear polarizer (-45° transmission)       | linearly polarized (-45°)         | 1         |
      | linearly polarized (-45°)         | quarter-wave plate (fast-axis vertical)    | circularly polarized (left hand)  | 1         |
      | linearly polarized (-45°)         | quarter-wave plate (fast-axis horizontal)  | circularly polarized (right hand) | 1         |
      | linearly polarized (-45°)         | half-wave plate                            | linearly polarized (+45°)         | 1         |
      | circularly polarized (right hand) | linear polarizer (horizontal transmission) | linearly polarized (horizontal)   | 1/2       |
      | circularly polarized (right hand) | linear polarizer (vertical transmission)   | linearly polarized (vertical)     | 1/2       |
      | circularly polarized (right hand) | linear polarizer (+45° transmission)       | linearly polarized (+45°)         | 1/2       |
      | circularly polarized (right hand) | linear polarizer (-45° transmission)       | linearly polarized (-45°)         | 1/2       |
      | circularly polarized (right hand) | quarter-wave plate (fast-axis vertical)    | linearly polarized (-45°)         | 1         |
      | circularly polarized (right hand) | quarter-wave plate (fast-axis horizontal)  | linearly polarized (+45°)         | 1         |
      | circularly polarized (right hand) | half-wave plate                            | circularly polarized (left hand)  | 1         |
      | circularly polarized (left hand)  | linear polarizer (horizontal transmission) | linearly polarized (horizontal)   | 1/2       |
      | circularly polarized (left hand)  | linear polarizer (vertical transmission)   | linearly polarized (vertical)     | 1/2       |
      | circularly polarized (left hand)  | linear polarizer (+45° transmission)       | linearly polarized (+45°)         | 1/2       |
      | circularly polarized (left hand)  | linear polarizer (-45° transmission)       | linearly polarized (-45°)         | 1/2       |
      | circularly polarized (left hand)  | quarter-wave plate (fast-axis vertical)    | linearly polarized (+45°)         | 1         |
      | circularly polarized (left hand)  | quarter-wave plate (fast-axis horizontal)  | linearly polarized (-45°)         | 1         |
      | circularly polarized (left hand)  | half-wave plate                            | circularly polarized (right hand) | 1         |

  Scenario: Unpolarized light passing through two linear filters at 90° from each other
    Given S is a Stokes vector for unpolarized light
    And S2 is a Stokes vector for no light
    When S passes through a linear polarizer (vertical transmission)
    And S passes through a linear polarizer (horizontal transmission)
    Then S = S2

  Scenario: Unpolarized light passing through three linear filters at 45° from each other
    Given S is a Stokes vector for unpolarized light
    And S2 is a Stokes vector for linearly polarized (horizontal) light
    And S2 ← S2 * 0.125
    When S passes through a linear polarizer (vertical transmission)
    And S passes through a linear polarizer (+45° transmission)
    And S passes through a linear polarizer (horizontal transmission)
    Then S = S2
