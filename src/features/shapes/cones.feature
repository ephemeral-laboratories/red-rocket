Feature: Cones

  Scenario Outline: Intersecting a cone with a ray
    Given shape ← cone()
    And direction ← normalize(<direction>)
    And ray ← ray(<origin>, direction)
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = <t0>
    And intersections[1].t = <t1>

    Examples:
      | origin          | direction           | t0      | t1 |
      | point(0, 0, -5) | vector(0, 0, 1)     | 5       | 5 |
      | point(0, 0, -5) | vector(1, 1, 1)     | 8.66025 | 8.66025 |
      | point(1, 1, -5) | vector(-0.5, -1, 1) | 4.55006 | 49.44994 |

  Scenario: Intersecting a cone with a ray parallel to one of its halves
    Given shape ← cone()
    And direction ← normalize(vector(0, 1, 1))
    And ray ← ray(point(0, 0, -1), direction)
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 1
    And intersections[0].t = 0.35355

  Scenario Outline: Intersecting a cone's end caps
    Given shape ← cone()
    And shape.minimum ← -0.5
    And shape.maximum ← 0.5
    And shape.closed ← true
    And direction ← normalize(<direction>)
    And ray ← ray(<origin>, direction)
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = <count>

    Examples:
      | origin             | direction       | count |
      | point(0, 0, -5)    | vector(0, 1, 0) |     0 |
      | point(0, 0, -0.25) | vector(0, 1, 1) |     2 |
      | point(0, 0, -0.25) | vector(0, 1, 0) |     4 |

  Scenario Outline: Computing the normal vector on a cone
    Given shape ← cone()
    When normal ← local_normal_at(shape, <point>)
    Then normal = <normal>

    Examples:
      | point            | normal            |
      | point(0, 0, 0)   | vector(0, 0, 0)   |
      | point(1, 1, 1)   | vector(1, -√2, 1) |
      | point(-1, -1, 0) | vector(-1, 1, 0)  |