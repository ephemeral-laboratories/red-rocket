Feature: Intersections

  Scenario: An intersection encapsulates t and object
    Given shape ← sphere()
    When intersection ← intersection(3.5, shape)
    Then intersection.t = 3.5
    And intersection.object = shape

  Scenario: Aggregating intersections
    Given shape ← sphere()
    And intersection1 ← intersection(1, shape)
    And intersection2 ← intersection(2, shape)
    When intersections ← intersections(intersection1, intersection2)
    Then intersections.count = 2
    And intersections[0].t = 1
    And intersections[1].t = 2

  Scenario: The hit, when all intersections have positive t
    Given shape ← sphere()
    And intersection1 ← intersection(1, shape)
    And intersection2 ← intersection(2, shape)
    And intersections ← intersections(intersection2, intersection1)
    When intersection ← hit(intersections)
    Then intersection = intersection1

  Scenario: The hit, when some intersections have negative t
    Given shape ← sphere()
    And intersection1 ← intersection(-1, shape)
    And intersection2 ← intersection(1, shape)
    And intersections ← intersections(intersection2, intersection1)
    When intersection ← hit(intersections)
    Then intersection = intersection2

  Scenario: The hit, when all intersections have negative t
    Given shape ← sphere()
    And intersection1 ← intersection(-2, shape)
    And intersection2 ← intersection(-1, shape)
    And intersections ← intersections(intersection2, intersection1)
    When intersection ← hit(intersections)
    Then intersection is nothing

  Scenario: The hit is always the lowest nonnegative intersection
    Given shape ← sphere()
    And intersection1 ← intersection(5, shape)
    And intersection2 ← intersection(7, shape)
    And intersection3 ← intersection(-3, shape)
    And intersection4 ← intersection(2, shape)
    And intersections ← intersections(intersection1, intersection2, intersection3, intersection4)
    When intersection ← hit(intersections)
    Then intersection = intersection4

  Scenario: Precomputing the state of an intersection
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← sphere()
    And intersection ← intersection(4, shape)
    When comps ← prepare_computations(intersection, ray)
    Then comps.t = intersection.t
    And comps.object = intersection.object
    And comps.point = point(0, 0, -1)
    And comps.eyeline = vector(0, 0, -1)
    And comps.normal = vector(0, 0, -1)

  Scenario: The hit, when an intersection occurs on the outside
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← sphere()
    And intersection ← intersection(4, shape)
    When comps ← prepare_computations(intersection, ray)
    Then comps.inside = false

  Scenario: The hit, when an intersection occurs on the inside
    Given ray ← ray(point(0, 0, 0), vector(0, 0, 1))
    And shape ← sphere()
    And intersection ← intersection(1, shape)
    When comps ← prepare_computations(intersection, ray)
    Then comps.point = point(0, 0, 1)
    And comps.eyeline = vector(0, 0, -1)
    And comps.inside = true
    # normal would have been (0, 0, 1), but is inverted!
    And comps.normal = vector(0, 0, -1)

  Scenario: The hit should offset the point
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← sphere() with:
      | transform | translation(0, 0, 1) |
    And intersection ← intersection(5, shape)
    When comps ← prepare_computations(intersection, ray)
    Then comps.over_point.z < -EPSILON/2
    And comps.point.z > comps.over_point.z

  Scenario: Precomputing the reflection vector
    Given shape ← plane()
    And ray ← ray(point(0, 1, -1), vector(0, -√2/2, √2/2))
    And intersection ← intersection(√2, shape)
    When comps ← prepare_computations(intersection, ray)
    Then comps.reflection = vector(0, √2/2, √2/2)

  Scenario: Computing the tangent vector
    Given shape ← plane()
    And ray ← ray(point(0, 1, -1), vector(0, -√2/2, √2/2))
    And intersection ← intersection(√2, shape)
    When comps ← prepare_computations(intersection, ray)
    Then comps.tangent = vector(1, 0, 0)

  Scenario: Computing the bitangent vector
    Given shape ← plane()
    And ray ← ray(point(0, 1, -1), vector(0, -√2/2, √2/2))
    And intersection ← intersection(√2, shape)
    When comps ← prepare_computations(intersection, ray)
    Then comps.bitangent = vector(0, 0, 1)

  Scenario Outline: Finding n1 and n2 at various intersections
    Given shape1 ← glass_sphere() with:
      | transform                 | scaling(2, 2, 2) |
      | material.refractive_index | 1.5              |
    And shape2 ← glass_sphere() with:
      | transform                 | translation(0, 0, -0.25) |
      | material.refractive_index | 2                        |
    And shape3 ← glass_sphere() with:
      | transform                 | translation(0, 0, 0.25) |
      | material.refractive_index | 2.5                     |
    And ray ← ray(point(0, 0, -4), vector(0, 0, 1))
    And intersections ← intersections(2:shape1, 2.75:shape2, 3.25:shape3, 4.75:shape2, 5.25:shape3, 6:shape1)
    When comps ← prepare_computations(intersections[<index>], ray, intersections)
    Then comps.n1 = <n1>
    And comps.n2 = <n2>

    Examples:
      | index | n1  | n2  |
      | 0     | 1   | 1.5 |
      | 1     | 1.5 | 2   |
      | 2     | 2   | 2.5 |
      | 3     | 2.5 | 2.5 |
      | 4     | 2.5 | 1.5 |
      | 5     | 1.5 | 1   |

  Scenario: The under point is offset below the surface
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← glass_sphere() with:
      | transform | translation(0, 0, 1) |
    And intersection ← intersection(5, shape)
    And intersections ← intersections(intersection)
    When comps ← prepare_computations(intersection, ray, intersections)
    Then comps.under_point.z > EPSILON/2
    And comps.point.z < comps.under_point.z

  Scenario: The Fresnel reflectance under total internal reflection
    Given shape ← glass_sphere()
    And ray ← ray(point(0, 0, √2/2), vector(0, 1, 0))
    And intersections ← intersections(-√2/2:shape, √2/2:shape)
    When comps ← prepare_computations(intersections[1], ray, intersections)
    And reflectance ← fresnel(comps)
    Then reflectance = 1

  Scenario: The Fresnel reflectance with a perpendicular viewing angle
    Given shape ← glass_sphere()
    And ray ← ray(point(0, 0, 0), vector(0, 1, 0))
    And intersections ← intersections(-1:shape, 1:shape)
    When comps ← prepare_computations(intersections[1], ray, intersections)
    And reflectance ← fresnel(comps)
    Then reflectance = 0.04

  Scenario: The Fresnel reflectance with small angle and n2 > n1
    Given shape ← glass_sphere()
    And ray ← ray(point(0, 0.99, -2), vector(0, 0, 1))
    And intersections ← intersections(1.8589:shape)
    When comps ← prepare_computations(intersections[0], ray, intersections)
    And reflectance ← fresnel(comps)
    Then reflectance = 0.45931275795

  Scenario: An intersection can encapsulate `u` and `v`
    Given shape ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
    When intersection ← intersection_with_uv(3.5, shape, 0.2, 0.4)
    Then intersection.u = 0.2
    And intersection.v = 0.4