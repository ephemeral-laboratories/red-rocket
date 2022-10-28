Feature: Groups

  Scenario: Creating a new group
    Given group ← group()
    Then group.transform = identity_matrix
    And group is empty

  Scenario: Adding a child to a group
    Given group ← group()
    And shape ← test_shape()
    When add_child(group, shape)
    Then group is not empty
    And group includes shape
    And shape.parent = group

  Scenario: Intersecting a ray with an empty group
    Given group ← group()
    And ray ← ray(point(0, 0, 0), vector(0, 0, 1))
    When intersections ← local_intersect(group, ray)
    Then intersections is empty

  Scenario: Intersecting a ray with a nonempty group
    Given group ← group()
    And shape1 ← sphere()
    And shape2 ← sphere()
    And set_transform(shape2, translation(0, 0, -3))
    And shape3 ← sphere()
    And set_transform(shape3, translation(5, 0, 0))
    And add_child(group, shape1)
    And add_child(group, shape2)
    And add_child(group, shape3)
    When ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And intersections ← local_intersect(group, ray)
    Then intersections.count = 4
    And intersections[0].object = shape2
    And intersections[1].object = shape2
    And intersections[2].object = shape1
    And intersections[3].object = shape1

  Scenario: Intersecting a transformed group
    Given group ← group()
    And set_transform(group, scaling(2, 2, 2))
    And shape ← sphere()
    And set_transform(shape, translation(5, 0, 0))
    And add_child(group, shape)
    When ray ← ray(point(10, 0, -10), vector(0, 0, 1))
    And intersections ← intersect(group, ray)
    Then intersections.count = 2