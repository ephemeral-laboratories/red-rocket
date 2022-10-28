Feature: Triangles

  Scenario: Constructing a triangle
    Given point1 ← point(0, 1, 0)
    And point2 ← point(-1, 0, 0)
    And point3 ← point(1, 0, 0)
    And shape ← triangle(point1, point2, point3)
    Then shape.point1 = point1
    And shape.point2 = point2
    And shape.point3 = point3
    And shape.edge1 = vector(-1, -1, 0)
    And shape.edge2 = vector(1, -1, 0)
    And shape.normal = vector(0, 0, -1)

  Scenario: Finding the normal on a triangle
    Given shape ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
    When normal1 ← local_normal_at(shape, point(0, 0.5, 0))
    And normal2 ← local_normal_at(shape, point(-0.5, 0.75, 0))
    And normal3 ← local_normal_at(shape, point(0.5, 0.25, 0))
    Then normal1 = shape.normal
    And normal2 = shape.normal
    And normal3 = shape.normal

  Scenario: Intersecting a ray parallel to the triangle
    Given shape ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
    And ray ← ray(point(0, -1, -2), vector(0, 1, 0))
    When intersections ← local_intersect(shape, ray)
    Then intersections is empty

  Scenario: A ray misses the point1-point3 edge
    Given shape ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
    And ray ← ray(point(1, 1, -2), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections is empty

  Scenario: A ray misses the point1-point2 edge
    Given shape ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
    And ray ← ray(point(-1, 1, -2), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections is empty

  Scenario: A ray misses the point2-point3 edge
    Given shape ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
    And ray ← ray(point(0, -1, -2), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections is empty

  Scenario: A ray strikes a triangle
    Given shape ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
    And ray ← ray(point(0, 0.5, -2), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 1
    And intersections[0].t = 2