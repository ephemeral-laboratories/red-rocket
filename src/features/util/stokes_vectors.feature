Feature: Stokes Vectors

  Scenario Outline: Stokes vector for various basic cases
    When S is a Stokes vector for <description> light
    Then S = <expected values>

    Examples:
      | description                       | expected values |
      | unpolarized                       | (1, 0, 0, 0)    |
      | linearly polarized (horizontal)   | (1, 1, 0, 0)    |
      | linearly polarized (vertical)     | (1, -1, 0, 0)   |
      | linearly polarized (+45°)         | (1, 0, 1, 0)    |
      | linearly polarized (-45°)         | (1, 0, -1, 0)   |
      | circularly polarized (right hand) | (1, 0, 0, 1)    |
      | circularly polarized (left hand)  | (1, 0, 0, -1)   |
