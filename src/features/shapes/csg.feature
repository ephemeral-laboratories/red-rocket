Feature: Constructive Solid Geometry

  Scenario: CSG is created with an operation and two shapes
    Given shape1 ← sphere()
    And shape2 ← cube()
    When shape ← csg("union", shape1, shape2)
    Then shape.operation = "union"
    And shape.left = shape1
    And shape.right = shape2
    And shape1.parent = shape
    And shape2.parent = shape

  Scenario Outline: Evaluating the rule for a CSG operation
    When result ← intersection_allowed("<op>", <lhit>, <inl>, <inr>)
    Then result = <result>
    Examples:
      | op           | lhit  | inl   | inr   | result |
      | union        | true  | true  | true  | false  |
      | union        | true  | true  | false | true   |
      | union        | true  | false | true  | false  |
      | union        | true  | false | false | true   |
      | union        | false | true  | true  | false  |
      | union        | false | true  | false | false  |
      | union        | false | false | true  | true   |
      | union        | false | false | false | true   |
      | intersection | true  | true  | true  | true   |
      | intersection | true  | true  | false | false  |
      | intersection | true  | false | true  | true   |
      | intersection | true  | false | false | false  |
      | intersection | false | true  | true  | true   |
      | intersection | false | true  | false | true   |
      | intersection | false | false | true  | false  |
      | intersection | false | false | false | false  |
      | difference   | true  | true  | true  | false  |
      | difference   | true  | true  | false | true   |
      | difference   | true  | false | true  | false  |
      | difference   | true  | false | false | true   |
      | difference   | false | true  | true  | true   |
      | difference   | false | true  | false | true   |
      | difference   | false | false | true  | false  |
      | difference   | false | false | false | false  |

  Scenario Outline: Filtering a list of intersections
    Given shape1 ← sphere()
    And shape2 ← cube()
    And shape ← csg("<operation>", shape1, shape2)
    And intersections ← intersections(1:shape1, 2:shape2, 3:shape1, 4:shape2)
    When result ← filter_intersections(shape, intersections)
    Then result.count = 2
    And result[0] = intersections[<x0>]
    And result[1] = intersections[<x1>]

    Examples:
      | operation    | x0 | x1 |
      | union        |  0 |  3 |
      | intersection |  1 |  2 |
      | difference   |  0 |  1 |

  Scenario: A ray misses a CSG object
    Given shape ← csg("union", sphere(), cube())
    And ray ← ray(point(0, 2, -5), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections is empty

  Scenario: A ray hits a CSG object
    Given shape1 ← sphere()
    And shape2 ← sphere()
    And set_transform(shape2, translation(0, 0, 0.5))
    And shape ← csg("union", shape1, shape2)
    And ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = 4
    And intersections[0].object = shape1
    And intersections[1].t = 6.5
    And intersections[1].object = shape2