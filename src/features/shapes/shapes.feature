Feature: Shapes

  Scenario: The default transformation
    Given shape ← test_shape()
    Then shape.transform = identity_matrix

  Scenario: Assigning a transformation
    Given shape ← test_shape()
    When set_transform(shape, translation(2, 3, 4))
    Then shape.transform = translation(2, 3, 4)

  Scenario: The default material
    Given shape ← test_shape()
    When material ← shape.material
    Then material = material()

  Scenario: Assigning a material
    Given shape ← test_shape()
    And material ← material()
    And material.ambient ← 1
    When shape.material ← material
    Then shape.material = material

  Scenario: Intersecting a scaled shape with a ray
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← test_shape()
    When set_transform(shape, scaling(2, 2, 2))
    And intersections ← intersect(shape, ray)
    Then shape.saved_ray.origin = point(0, 0, -2.5)
    And shape.saved_ray.direction = vector(0, 0, 0.5)

  Scenario: Intersecting a translated shape with a ray
    Given ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← test_shape()
    When set_transform(shape, translation(5, 0, 0))
    And intersections ← intersect(shape, ray)
    Then shape.saved_ray.origin = point(-5, 0, -5)
    And shape.saved_ray.direction = vector(0, 0, 1)

  Scenario: Computing the normal on a translated shape
    Given shape ← test_shape()
    When set_transform(shape, translation(0, 1, 0))
    And normal ← normal_at(shape, point(0, 1.70711, -0.70711))
    Then normal = vector(0, 0.70711, -0.70711)

  Scenario: Computing the normal on a transformed shape
    Given shape ← test_shape()
    And transform ← scaling(1, 0.5, 1) * rotation_z(π/5)
    When set_transform(shape, transform)
    And normal ← normal_at(shape, point(0, √2/2, -√2/2))
    Then normal = vector(0, 0.97014, -0.24254)

  Scenario: A shape has a parent attribute
    Given shape ← test_shape()
    Then shape.parent is nothing

  Scenario: Converting a point from world to object space
    Given group1 ← group()
    And set_transform(group1, rotation_y(π/2))
    And group2 ← group()
    And set_transform(group2, scaling(2, 2, 2))
    And add_child(group1, group2)
    And shape ← sphere()
    And set_transform(shape, translation(5, 0, 0))
    And add_child(group2, shape)
    When point ← world_to_object(shape, point(-2, 0, -10))
    Then point = point(0, 0, -1)

  Scenario: Converting a normal from object to world space
    Given group1 ← group()
    And set_transform(group1, rotation_y(π/2))
    And group2 ← group()
    And set_transform(group2, scaling(1, 2, 3))
    And add_child(group1, group2)
    And shape ← sphere()
    And set_transform(shape, translation(5, 0, 0))
    And add_child(group2, shape)
    When normal ← normal_to_world(shape, vector(√3/3, √3/3, √3/3))
    Then normal = vector(0.2857, 0.4286, -0.8571)

  Scenario: Finding the normal on a child object
    Given group1 ← group()
    And set_transform(group1, rotation_y(π/2))
    And group2 ← group()
    And set_transform(group2, scaling(1, 2, 3))
    And add_child(group1, group2)
    And shape ← sphere()
    And set_transform(shape, translation(5, 0, 0))
    And add_child(group2, shape)
    When normal ← normal_at(shape, point(1.7321, 1.1547, -5.5774))
    Then normal = vector(0.2857, 0.4286, -0.8571)