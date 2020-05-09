Feature: Matrices

  Scenario: Constructing and inspecting a 4x4 matrix
    Given the following 4x4 matrix M:
      |  1.0 |  2.0 |  3.0 |  4.0 |
      |  5.5 |  6.5 |  7.5 |  8.5 |
      |  9.0 | 10.0 | 11.0 | 12.0 |
      | 13.5 | 14.5 | 15.5 | 16.5 |
    Then M[0, 0] = 1.0
    And M[0, 3] = 4.0
    And M[1, 0] = 5.5
    And M[1, 2] = 7.5
    And M[2, 2] = 11.0
    And M[3, 0] = 13.5
    And M[3, 2] = 15.5

  Scenario: A 2x2 matrix ought to be representable
    Given the following 2x2 matrix M:
      | -3.0 |  5.0 |
      |  1.0 | -2.0 |
    Then M[0, 0] = -3.0
    And M[0, 1] = 5.0
    And M[1, 0] = 1.0
    And M[1, 1] = -2.0

  Scenario: A 3x3 matrix ought to be representable
    Given the following 3x3 matrix M:
      | -3.0 |  5.0 |  0.0 |
      |  1.0 | -2.0 | -7.0 |
      |  0.0 |  1.0 |  1.0 |
    Then M[0, 0] = -3.0
    And M[1, 1] = -2.0
    And M[2, 2] = 1.0

  Scenario: Matrix equality with identical matrices
    Given the following matrix A:
      | 1.0 | 2.0 | 3.0 | 4.0 |
      | 5.0 | 6.0 | 7.0 | 8.0 |
      | 9.0 | 8.0 | 7.0 | 6.0 |
      | 5.0 | 4.0 | 3.0 | 2.0 |
    And the following matrix B:
      | 1.0 | 2.0 | 3.0 | 4.0 |
      | 5.0 | 6.0 | 7.0 | 8.0 |
      | 9.0 | 8.0 | 7.0 | 6.0 |
      | 5.0 | 4.0 | 3.0 | 2.0 |
    Then A = B

  Scenario: Matrix equality with different matrices
    Given the following matrix A:
      | 1.0 | 2.0 | 3.0 | 4.0 |
      | 5.0 | 6.0 | 7.0 | 8.0 |
      | 9.0 | 8.0 | 7.0 | 6.0 |
      | 5.0 | 4.0 | 3.0 | 2.0 |
    And the following matrix B:
      | 2.0 | 3.0 | 4.0 | 5.0 |
      | 6.0 | 7.0 | 8.0 | 9.0 |
      | 8.0 | 7.0 | 6.0 | 5.0 |
      | 4.0 | 3.0 | 2.0 | 1.0 |
    Then A != B

  Scenario: Multiplying two matrices
    Given the following matrix A:
      | 1.0 | 2.0 | 3.0 | 4.0 |
      | 5.0 | 6.0 | 7.0 | 8.0 |
      | 9.0 | 8.0 | 7.0 | 6.0 |
      | 5.0 | 4.0 | 3.0 | 2.0 |
    And the following matrix B:
      | -2.0 | 1.0 | 2.0 |  3.0 |
      |  3.0 | 2.0 | 1.0 | -1.0 |
      |  4.0 | 3.0 | 6.0 |  5.0 |
      |  1.0 | 2.0 | 7.0 |  8.0 |
    Then A * B is the following 4x4 matrix:
      | 20.0 | 22.0 |  50.0 |  48.0 |
      | 44.0 | 54.0 | 114.0 | 108.0 |
      | 40.0 | 58.0 | 110.0 | 102.0 |
      | 16.0 | 26.0 |  46.0 |  42.0 |

  Scenario: A matrix multiplied by a tuple
    Given the following matrix A:
      | 1.0 | 2.0 | 3.0 | 4.0 |
      | 2.0 | 4.0 | 4.0 | 2.0 |
      | 8.0 | 6.0 | 4.0 | 1.0 |
      | 0.0 | 0.0 | 0.0 | 1.0 |
    And b ← tuple(1.0, 2.0, 3.0, 1.0)
    Then A * b = tuple(18.0, 24.0, 33.0, 1.0)

  Scenario: Multiplying a matrix by the identity matrix
    Given the following matrix A:
      | 0.0 | 1.0 |  2.0 |  4.0 |
      | 1.0 | 2.0 |  4.0 |  8.0 |
      | 2.0 | 4.0 |  8.0 | 16.0 |
      | 4.0 | 8.0 | 16.0 | 32.0 |
    Then A * I = A

  Scenario: Multiplying the identity matrix by a tuple
    Given a ← tuple(1.0, 2.0, 3.0, 4.0)
    Then I * a = a

  Scenario: Transposing a matrix
    Given the following matrix A:
      | 0.0 | 9.0 | 3.0 | 0.0 |
      | 9.0 | 8.0 | 0.0 | 8.0 |
      | 1.0 | 8.0 | 5.0 | 3.0 |
      | 0.0 | 0.0 | 5.0 | 8.0 |
    Then transpose(A) is the following matrix:
      | 0.0 | 9.0 | 1.0 | 0.0 |
      | 9.0 | 8.0 | 8.0 | 0.0 |
      | 3.0 | 0.0 | 5.0 | 5.0 |
      | 0.0 | 8.0 | 3.0 | 8.0 |

  Scenario: Transposing the identity matrix
    Given A ← transpose(I)
    Then A = I

  Scenario: Calculating the determinant of a 2x2 matrix
    Given the following 2x2 matrix A:
      |  1.0 | 5.0 |
      | -3.0 | 2.0 |
    Then determinant(A) = 17.0

  Scenario: A submatrix of a 3x3 matrix is a 2x2 matrix
    Given the following 3x3 matrix A:
      |  1.0 | 5.0 |  0.0 |
      | -3.0 | 2.0 |  7.0 |
      |  0.0 | 6.0 | -3.0 |
    Then submatrix(A, 0, 2) is the following 2x2 matrix:
      | -3.0 | 2.0 |
      |  0.0 | 6.0 |

  Scenario: A submatrix of a 4x4 matrix is a 3x3 matrix
    Given the following 4x4 matrix A:
      | -6.0 | 1.0 |  1.0 | 6.0 |
      | -8.0 | 5.0 |  8.0 | 6.0 |
      | -1.0 | 0.0 |  8.0 | 2.0 |
      | -7.0 | 1.0 | -1.0 | 1.0 |
    Then submatrix(A, 2, 1) is the following 3x3 matrix:
      | -6.0 |  1.0 | 6.0 |
      | -8.0 |  8.0 | 6.0 |
      | -7.0 | -1.0 | 1.0 |

  Scenario: Calculating a minor of a 3x3 matrix
    Given the following 3x3 matrix A:
      | 3.0 |  5.0 |  0.0 |
      | 2.0 | -1.0 | -7.0 |
      | 6.0 | -1.0 |  5.0 |
    And B ← submatrix(A, 1, 0)
    Then determinant(B) = 25.0
    And minor(A, 1, 0) = 25.0

  Scenario: Calculating a cofactor of a 3x3 matrix
    Given the following 3x3 matrix A:
      | 3.0 |  5.0 |  0.0 |
      | 2.0 | -1.0 | -7.0 |
      | 6.0 | -1.0 |  5.0 |
    Then minor(A, 0, 0) = -12.0
    And cofactor(A, 0, 0) = -12.0
    And minor(A, 1, 0) = 25.0
    And cofactor(A, 1, 0) = -25.0

  Scenario: Calculating the determinant of a 3x3 matrix
    Given the following 3x3 matrix A:
      |  1.0 | 2.0 |  6.0 |
      | -5.0 | 8.0 | -4.0 |
      |  2.0 | 6.0 |  4.0 |
    Then cofactor(A, 0, 0) = 56.0
    And cofactor(A, 0, 1) = 12.0
    And cofactor(A, 0, 2) = -46.0
    And determinant(A) = -196.0

  Scenario: Calculating the determinant of a 4x4 matrix
    Given the following 4x4 matrix A:
      | -2.0 | -8.0 |  3.0 |  5.0 |
      | -3.0 |  1.0 |  7.0 |  3.0 |
      |  1.0 |  2.0 | -9.0 |  6.0 |
      | -6.0 |  7.0 |  7.0 | -9.0 |
    Then cofactor(A, 0, 0) = 690.0
    And cofactor(A, 0, 1) = 447.0
    And cofactor(A, 0, 2) = 210.0
    And cofactor(A, 0, 3) = 51.0
    And determinant(A) = -4071.0

  Scenario: Testing an invertible matrix for invertibility
    Given the following 4x4 matrix A:
      | 6.0 |  4.0 | 4.0 |  4.0 |
      | 5.0 |  5.0 | 7.0 |  6.0 |
      | 4.0 | -9.0 | 3.0 | -7.0 |
      | 9.0 |  1.0 | 7.0 | -6.0 |
    Then determinant(A) = -2120.0
    And A is invertible

  Scenario: Testing a noninvertible matrix for invertibility
    Given the following 4x4 matrix A:
      | -4.0 |  2.0 | -2.0 | -3.0 |
      |  9.0 |  6.0 |  2.0 |  6.0 |
      |  0.0 | -5.0 |  1.0 | -5.0 |
      |  0.0 |  0.0 |  0.0 |  0.0 |
    Then determinant(A) = 0.0
    And A is not invertible

  Scenario: Calculating the inverse of a matrix
    Given the following 4x4 matrix A:
      | -5.0 |  2.0 |  6.0 | -8.0 |
      |  1.0 | -5.0 |  1.0 |  8.0 |
      |  7.0 |  7.0 | -6.0 | -7.0 |
      |  1.0 | -3.0 |  7.0 |  4.0 |
    And B ← inverse(A).
    Then determinant(A) = 532.0
    And cofactor(A, 2, 3) = -160.0
    And B[3, 2] = -160.0/532.0
    And cofactor(A, 3, 2) = 105.0
    And B[2, 3] = 105.0/532.0
    And B is the following 4x4 matrix:
      |  0.21805 |  0.45113 |  0.24060 | -0.04511 |
      | -0.80827 | -1.45677 | -0.44361 |  0.52068 |
      | -0.07895 | -0.22368 | -0.05263 |  0.19737 |
      | -0.52256 | -0.81391 | -0.30075 |  0.30639 |

  Scenario: Calculating the inverse of another matrix
    Given the following 4x4 matrix A:
      |  8.0 | -5.0 |  9.0 |  2.0 |
      |  7.0 |  5.0 |  6.0 |  1.0 |
      | -6.0 |  0.0 |  9.0 |  6.0 |
      | -3.0 |  0.0 | -9.0 | -4.0 |
    Then inverse(A) is the following 4x4 matrix:
      | -0.15385 | -0.15385 | -0.28205 | -0.53846 |
      | -0.07692 |  0.12308 |  0.02564 |  0.03077 |
      |  0.35897 |  0.35897 |  0.43590 |  0.92308 |
      | -0.69231 | -0.69231 | -0.76923 | -1.92308 |

  Scenario: Calculating the inverse of a third matrix
    Given the following 4x4 matrix A:
      |  9.0 |  3.0 |  0.0 |  9.0 |
      | -5.0 | -2.0 | -6.0 | -3.0 |
      | -4.0 |  9.0 |  6.0 |  4.0 |
      | -7.0 |  6.0 |  6.0 |  2.0 |
    Then inverse(A) is the following 4x4 matrix:
      | -0.04074 | -0.07778 |  0.14444 | -0.22222 |
      | -0.07778 |  0.03333 |  0.36667 | -0.33333 |
      | -0.02901 | -0.14630 | -0.10926 |  0.12963 |
      |  0.17778 |  0.06667 | -0.26667 |  0.33333 |

  Scenario: Multiplying a product by its inverse
    Given the following 4x4 matrix A:
      |  3.0 | -9.0 |  7.0 |  3.0 |
      |  3.0 | -8.0 |  2.0 | -9.0 |
      | -4.0 |  4.0 |  4.0 |  1.0 |
      | -6.0 |  5.0 | -1.0 |  1.0 |
    And the following 4x4 matrix B:
      | 8.0 |  2.0 | 2.0 | 2.0 |
      | 3.0 | -1.0 | 7.0 | 0.0 |
      | 7.0 |  0.0 | 5.0 | 4.0 |
      | 6.0 | -2.0 | 0.0 | 5.0 |
    And C ← A * B
    Then C * inverse(B) = A
