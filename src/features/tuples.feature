Feature: Tuples

  Scenario: A tuple with w=1.0 is a point
    Given tuple ← tuple(4.3, -4.2, 3.1, 1)
    Then tuple.x = 4.3
    And tuple.y = -4.2
    And tuple.z = 3.1
    And tuple.w = 1
    And tuple is a point
    And tuple is not a vector

  Scenario: A tuple with w=0 is a vector
    Given tuple ← tuple(4.3, -4.2, 3.1, 0)
    Then tuple.x = 4.3
    And tuple.y = -4.2
    And tuple.z = 3.1
    And tuple.w = 0
    And tuple is not a point
    And tuple is a vector

  Scenario: point() creates tuples with w=1
    Given point ← point(4, -4, 3)
    Then point = tuple(4, -4, 3, 1)

  Scenario: vector() creates tuples with w=0
    Given vector ← vector(4, -4, 3)
    Then vector = tuple(4, -4, 3, 0)

  Scenario: Adding two tuples
    Given tuple1 ← tuple(3, -2, 5, 1)
    And tuple2 ← tuple(-2, 3, 1, 0)
    Then tuple1 + tuple2 = tuple(1, 1, 6, 1)

  Scenario: Subtracting two points
    Given point1 ← point(3, 2, 1)
    And point2 ← point(5, 6, 7)
    Then point1 - point2 = vector(-2, -4, -6)

  Scenario: Subtracting a vector from a point
    Given point ← point(3, 2, 1)
    And vector ← vector(5, 6, 7)
    Then point - vector = point(-2, -4, -6)

  Scenario: Subtracting two vectors
    Given vector1 ← vector(3, 2, 1)
    And vector2 ← vector(5, 6, 7)
    Then vector1 - vector2 = vector(-2, -4, -6)

  Scenario: Subtracting a vector from the zero vector
    Given zero ← vector(0, 0, 0)
    And vector ← vector(1, -2, 3)
    Then zero - vector = vector(-1, 2, -3)

  Scenario: Negating a tuple
    Given tuple ← tuple(1, -2, 3, -4)
    Then -tuple = tuple(-1, 2, -3, 4)

  Scenario: Multiplying a tuple by a scalar
    Given tuple ← tuple(1, -2, 3, -4)
    Then tuple * 3.5 = tuple(3.5, -7, 10.5, -14)

  Scenario: Multiplying a tuple by a fraction
    Given tuple ← tuple(1, -2, 3, -4)
    Then tuple * 0.5 = tuple(0.5, -1, 1.5, -2)

  Scenario: Dividing a tuple by a scalar
    Given tuple ← tuple(1, -2, 3, -4)
    Then tuple / 2 = tuple(0.5, -1, 1.5, -2)

  Scenario: Computing the magnitude of vector(1, 0, 0)
    Given vector ← vector(1, 0, 0)
    Then magnitude(vector) = 1

  Scenario: Computing the magnitude of vector(0, 1, 0)
    Given vector ← vector(0, 1, 0)
    Then magnitude(vector) = 1

  Scenario: Computing the magnitude of vector(0, 0, 1)
    Given vector ← vector(0, 0, 1)
    Then magnitude(vector) = 1

  Scenario: Computing the magnitude of vector(1, 2, 3)
    Given vector ← vector(1, 2, 3)
    Then magnitude(vector) = √14

  Scenario: Computing the magnitude of vector(-1, -2, -3)
    Given vector ← vector(-1, -2, -3)
    Then magnitude(vector) = √14

  Scenario: Normalizing vector(4, 0, 0) gives (1, 0, 0)
    Given vector ← vector(4, 0, 0)
    Then normalize(vector) = vector(1, 0, 0)

  Scenario: Normalizing vector(1, 2, 3)
    Given vector ← vector(1, 2, 3)
                                        # vector(1/√14, 2/√14, 3/√14)
    Then normalize(vector) = approximately vector(0.26726, 0.53452, 0.80178)

  Scenario: The magnitude of a normalized vector
    Given vector ← vector(1, 2, 3)
    When normal ← normalize(vector)
    Then magnitude(normal) = 1

  Scenario: The dot product of two tuples
    Given vector1 ← vector(1, 2, 3)
    And vector2 ← vector(2, 3, 4)
    Then dot(vector1, vector2) = 20

  Scenario: The cross product of two vectors
    Given vector1 ← vector(1, 2, 3)
    And vector2 ← vector(2, 3, 4)
    Then cross(vector1, vector2) = vector(-1, 2, -1)
    And cross(vector2, vector1) = vector(1, -2, 1)

  Scenario: Colors are (red, green, blue) tuples
    Given color ← color(-0.5, 0.4, 1.7)
    Then color.red = -0.5
    And color.green = 0.4
    And color.blue = 1.7

  Scenario: Adding colors
    Given color1 ← color(0.9, 0.6, 0.75)
    And color2 ← color(0.7, 0.1, 0.25)
    Then color1 + color2 = color(1.6, 0.7, 1)

  Scenario: Subtracting colors
    Given color1 ← color(0.9, 0.6, 0.75)
    And color2 ← color(0.7, 0.1, 0.25)
    Then color1 - color2 = color(0.2, 0.5, 0.5)

  Scenario: Multiplying a color by a scalar
    Given color ← color(0.2, 0.3, 0.4)
    Then color * 2 = color(0.4, 0.6, 0.8)

  Scenario: Multiplying colors
    Given color1 ← color(1, 0.2, 0.4)
    And color2 ← color(0.9, 1, 0.1)
    Then color1 * color2 = color(0.9, 0.2, 0.04)

  Scenario: Reflecting a vector approaching at 45°
    Given vector ← vector(1, -1, 0)
    And normal ← vector(0, 1, 0)
    When reflection ← reflect(vector, normal)
    Then reflection = vector(1, 1, 0)

  Scenario: Reflecting a vector off a slanted surface
    Given vector ← vector(0, -1, 0)
    And normal ← vector(√2/2, √2/2, 0)
    When reflection ← reflect(vector, normal)
    Then reflection = vector(1, 0, 0)