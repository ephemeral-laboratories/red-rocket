Feature: Spheres

  Scenario: A ray intersects a sphere at two points
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← sphere()
    When intersections ← intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = 4
    And intersections[1].t = 6

  Scenario: A ray intersects a sphere at a tangent
    Given ray ← ray(point(0, 1, -5), vector(0, 0, 1))
    And shape ← sphere()
    When intersections ← intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = 5
    And intersections[1].t = 5

  Scenario: A ray misses a sphere
    Given ray ← ray(point(0, 2, -5), vector(0, 0, 1))
    And shape ← sphere()
    When intersections ← intersect(shape, ray)
    Then intersections.count = 0

  Scenario: A ray originates inside a sphere
    Given ray ← ray(point(0, 0, 0), vector(0, 0, 1))
    And shape ← sphere()
    When intersections ← intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = -1
    And intersections[1].t = 1

  Scenario: A sphere is behind a ray
    Given ray ← ray(point(0, 0, 5), vector(0, 0, 1))
    And shape ← sphere()
    When intersections ← intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = -6
    And intersections[1].t = -4

  Scenario: Intersect sets the object on the intersection
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← sphere()
    When intersections ← intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].object = shape
    And intersections[1].object = shape

  Scenario: A sphere'shape default transformation
    Given shape ← sphere()
    Then shape.transform = identity_matrix

  Scenario: Changing a sphere'shape transformation
    Given shape ← sphere()
    And transform ← translation(2, 3, 4)
    When set_transform(shape, transform)
    Then shape.transform = transform

  Scenario: Intersecting a scaled sphere with a ray
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← sphere()
    When set_transform(shape, scaling(2, 2, 2))
    And intersections ← intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = 3
    And intersections[1].t = 7

  Scenario: Intersecting a translated sphere with a ray
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← sphere()
    When set_transform(shape, translation(5, 0, 0))
    And intersections ← intersect(shape, ray)
    Then intersections.count = 0

  Scenario: The normal on a sphere at a point on the x axis
    Given shape ← sphere()
    When normal ← normal_at(shape, point(1, 0, 0))
    Then normal = vector(1, 0, 0)

  Scenario: The normal on a sphere at a point on the y axis
    Given shape ← sphere()
    When normal ← normal_at(shape, point(0, 1, 0))
    Then normal = vector(0, 1, 0)

  Scenario: The normal on a sphere at a point on the z axis
    Given shape ← sphere()
    When normal ← normal_at(shape, point(0, 0, 1))
    Then normal = vector(0, 0, 1)

  Scenario: The normal on a sphere at a nonaxial point
    Given shape ← sphere()
    When normal ← normal_at(shape, point(√3/3, √3/3, √3/3))
    Then normal = vector(√3/3, √3/3, √3/3)

  Scenario: The normal is a normalized vector
    Given shape ← sphere()
    When normal ← normal_at(shape, point(√3/3, √3/3, √3/3))
    Then normal = normalize(normal)

  Scenario: Computing the normal on a translated sphere
    Given shape ← sphere()
    And set_transform(shape, translation(0, 1, 0))
    When normal ← normal_at(shape, point(0, 1.70711, -0.70711))
    Then normal = vector(0, 0.70711, -0.70711)

  Scenario: Computing the normal on a transformed sphere
    Given shape ← sphere()
    And transform ← scaling(1, 0.5, 1) * rotation_z(π/5)
    And set_transform(shape, transform)
    When normal ← normal_at(shape, point(0, √2/2, -√2/2))
    Then normal = vector(0, 0.97014, -0.24254)

  Scenario: A sphere has a default material
    Given shape ← sphere()
    When material ← shape.material
    Then material = material()

  Scenario: A sphere may be assigned a material
    Given shape ← sphere()
    And material ← material()
    And material.ambient ← 1
    When shape.material ← material
    Then shape.material = material