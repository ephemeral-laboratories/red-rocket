Feature: Planes

  Scenario: The normal of a plane is constant everywhere
    Given shape ← plane()
    When normal1 ← local_normal_at(shape, point(0, 0, 0))
    And normal2 ← local_normal_at(shape, point(10, 0, -10))
    And normal3 ← local_normal_at(shape, point(-5, 0, 150))
    Then normal1 = vector(0, 1, 0)
    And normal2 = vector(0, 1, 0)
    And normal3 = vector(0, 1, 0)

  Scenario: Intersect with a ray parallel to the plane
    Given shape ← plane()
    And ray ← ray(point(0, 10, 0), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections is empty

  Scenario: Intersect with a coplanar ray
    Given shape ← plane()
    And ray ← ray(point(0, 0, 0), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections is empty

  Scenario: A ray intersecting a plane from above
    Given shape ← plane()
    And ray ← ray(point(0, 1, 0), vector(0, -1, 0))
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 1
    And intersections[0].t = 1
    And intersections[0].object = shape

  Scenario: A ray intersecting a plane from below
    Given shape ← plane()
    And ray ← ray(point(0, -1, 0), vector(0, 1, 0))
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 1
    And intersections[0].t = 1
    And intersections[0].object = shape