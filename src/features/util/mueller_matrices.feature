Feature: Mueller Matrices

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
