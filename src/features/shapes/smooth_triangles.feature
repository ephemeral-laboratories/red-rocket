Feature: Smooth Triangles

  Background:
    Given point1 ← point(0, 1, 0)
    And point2 ← point(-1, 0, 0)
    And point3 ← point(1, 0, 0)
    And normal1 ← vector(0, 1, 0)
    And normal2 ← vector(-1, 0, 0)
    And normal3 ← vector(1, 0, 0)
    When shape ← smooth_triangle(point1, point2, point3, normal1, normal2, normal3)

  Scenario: Constructing a smooth triangle
    Then shape.point1 = point1
    And shape.point2 = point2
    And shape.point3 = point3
    And shape.normal1 = normal1
    And shape.normal2 = normal2
    And shape.normal3 = normal3

  Scenario: An intersection with a smooth triangle stores u/v
    When ray ← ray(point(-0.2, 0.3, -2), vector(0, 0, 1))
    And intersections ← local_intersect(shape, ray)
    Then intersections[0].u = 0.45
    And intersections[0].v = 0.25

  Scenario: A smooth triangle uses u/v to interpolate the normal
    When intersection ← intersection_with_uv(1, shape, 0.45, 0.25)
    And normal ← normal_at(shape, point(0, 0, 0), intersection)
    Then normal = vector(-0.5547, 0.83205, 0)

  Scenario: Preparing the normal on a smooth triangle
    When intersection ← intersection_with_uv(1, shape, 0.45, 0.25)
    And ray ← ray(point(-0.2, 0.3, -2), vector(0, 0, 1))
    And intersections ← intersections(intersection)
    And comps ← prepare_computations(intersection, ray, intersections)
    Then comps.normal = vector(-0.5547, 0.83205, 0)