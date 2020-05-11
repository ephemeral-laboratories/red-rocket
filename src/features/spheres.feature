Feature: Spheres

  Scenario: A ray intersects a sphere at two points
    Given r ← ray(point(0, 0, -5), vector(0, 0, 1))
    And s ← sphere()
    When xs ← intersect(s, r)
    Then xs.count = 2
    And xs[0].t = 4.0
    And xs[1].t = 6.0

  Scenario: A ray intersects a sphere at a tangent
    Given r ← ray(point(0, 1, -5), vector(0, 0, 1))
    And s ← sphere()
    When xs ← intersect(s, r)
    Then xs.count = 2
    And xs[0].t = 5.0
    And xs[1].t = 5.0

  Scenario: A ray misses a sphere
    Given r ← ray(point(0, 2, -5), vector(0, 0, 1))
    And s ← sphere()
    When xs ← intersect(s, r)
    Then xs.count = 0

  Scenario: A ray originates inside a sphere
    Given r ← ray(point(0, 0, 0), vector(0, 0, 1))
    And s ← sphere()
    When xs ← intersect(s, r)
    Then xs.count = 2
    And xs[0].t = -1.0
    And xs[1].t = 1.0

  Scenario: A sphere is behind a ray
    Given r ← ray(point(0, 0, 5), vector(0, 0, 1))
    And s ← sphere()
    When xs ← intersect(s, r)
    Then xs.count = 2
    And xs[0].t = -6.0
    And xs[1].t = -4.0

  Scenario: Intersect sets the object on the intersection
    Given r ← ray(point(0, 0, -5), vector(0, 0, 1))
    And s ← sphere()
    When xs ← intersect(s, r)
    Then xs.count = 2
    And xs[0].object = s
    And xs[1].object = s

  Scenario: A sphere's default transformation
    Given s ← sphere()
    Then s.transform = identity_matrix

  Scenario: Changing a sphere's transformation
    Given s ← sphere()
    And t ← translation(2, 3, 4)
    When set_transform(s, t)
    Then s.transform = t

  Scenario: Intersecting a scaled sphere with a ray
    Given r ← ray(point(0, 0, -5), vector(0, 0, 1))
    And s ← sphere()
    When set_transform(s, scaling(2, 2, 2))
    And xs ← intersect(s, r)
    Then xs.count = 2
    And xs[0].t = 3
    And xs[1].t = 7

  Scenario: Intersecting a translated sphere with a ray
    Given r ← ray(point(0, 0, -5), vector(0, 0, 1))
    And s ← sphere()
    When set_transform(s, translation(5, 0, 0))
    And xs ← intersect(s, r)
    Then xs.count = 0
