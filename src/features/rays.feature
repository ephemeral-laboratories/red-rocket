Feature: Rays

  Scenario: Creating and querying a ray
    Given origin ← point(1, 2, 3)
    And direction ← vector(4, 5, 6)
    When ray ← ray(origin, direction)
    Then ray.origin = origin
    And ray.direction = direction

  Scenario: Computing a point from a distance
    Given ray ← ray(point(2, 3, 4), vector(1, 0, 0))
    Then position(ray, 0) = point(2, 3, 4)
    And position(ray, 1) = point(3, 3, 4)
    And position(ray, -1) = point(1, 3, 4)
    And position(ray, 2.5) = point(4.5, 3, 4)

  Scenario: Translating a ray
    Given ray ← ray(point(1, 2, 3), vector(0, 1, 0))
    And transform ← translation(3, 4, 5)
    When ray2 ← transform(ray, transform)
    Then ray2.origin = point(4, 6, 8)
    And ray2.direction = vector(0, 1, 0)

  Scenario: Scaling a ray
    Given ray ← ray(point(1, 2, 3), vector(0, 1, 0))
    And transform ← scaling(2, 3, 4)
    When ray2 ← transform(ray, transform)
    Then ray2.origin = point(2, 6, 12)
    And ray2.direction = vector(0, 3, 0)