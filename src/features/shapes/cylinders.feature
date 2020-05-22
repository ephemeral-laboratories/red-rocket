Feature: Cylinders

  Scenario Outline: A ray misses a cylinder
    Given shape ← cylinder()
    And direction ← normalize(<direction>)
    And ray ← ray(<origin>, direction)
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 0

    Examples:
      | origin          | direction       |
      | point(1, 0, 0)  | vector(0, 1, 0) |
      | point(0, 0, 0)  | vector(0, 1, 0) |
      | point(0, 0, -5) | vector(1, 1, 1) |

  Scenario Outline: A ray strikes a cylinder
    Given shape ← cylinder()
    And direction ← normalize(<direction>)
    And ray ← ray(<origin>, direction)
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = 2
    And intersections[0].t = <t0>
    And intersections[1].t = <t1>

    Examples:
      | origin            | direction         | t0      | t1      |
      | point(1,   0, -5) | vector(0,   0, 1) | 5       | 5       |
      | point(0,   0, -5) | vector(0,   0, 1) | 4       | 6       |
      | point(0.5, 0, -5) | vector(0.1, 1, 1) | 6.80798 | 7.08872 |

  Scenario Outline: Normal vector on a cylinder
    Given shape ← cylinder()
    When normal ← local_normal_at(shape, <point>)
    Then normal = <normal>

    Examples:
      | point           | normal           |
      | point(1, 0, 0)  | vector(1, 0, 0)  |
      | point(0, 5, -1) | vector(0, 0, -1) |
      | point(0, -2, 1) | vector(0, 0, 1)  |
      | point(-1, 1, 0) | vector(-1, 0, 0) |

  Scenario: The default minimum and maximum for a cylinder
    Given shape ← cylinder()
    Then shape.minimum = -∞
    And shape.maximum = ∞

  Scenario Outline: Intersecting a constrained cylinder
    Given shape ← cylinder() with:
      | minimum | 1 |
      | maximum | 2 |
    And direction ← normalize(<direction>)
    And ray ← ray(<point>, direction)
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = <count>

    Examples:
      | point             | direction         | count |
      | point(0, 1.5,  0) | vector(0.1, 1, 0) | 0     |
      | point(0, 3,   -5) | vector(0, 0, 1)   | 0     |
      | point(0, 0,   -5) | vector(0, 0, 1)   | 0     |
      | point(0, 2,   -5) | vector(0, 0, 1)   | 0     |
      | point(0, 1,   -5) | vector(0, 0, 1)   | 0     |
      | point(0, 1.5, -2) | vector(0, 0, 1)   | 2     |

  Scenario: The default closed value for a cylinder
    Given shape ← cylinder()
    Then shape.closed = false

  Scenario Outline: Intersecting the caps of a closed cylinder
    Given shape ← cylinder() with:
      | minimum | 1    |
      | maximum | 2    |
      | closed  | true |
    And direction ← normalize(<direction>)
    And ray ← ray(<point>, direction)
    When intersections ← local_intersect(shape, ray)
    Then intersections.count = <count>

    Examples:
      | point            | direction        | count |
      | point(0, 3, 0)   | vector(0, -1, 0) |     2 |
      | point(0, 3, -2)  | vector(0, -1, 2) |     2 |
      | point(0, 4, -2)  | vector(0, -1, 1) |     2 | # corner case
      | point(0, 0, -2)  | vector(0, 1, 2)  |     2 |
      | point(0, -1, -2) | vector(0, 1, 1)  |     2 | # corner case

  Scenario Outline: The normal vector on a cylinder's end caps
    Given shape ← cylinder() with:
      | minimum | 1    |
      | maximum | 2    |
      | closed  | true |
    When normal ← local_normal_at(shape, <point>)
    Then normal = <normal>

    Examples:
      | point            | normal           |
      | point(0, 1, 0)   | vector(0, -1, 0) |
      | point(0.5, 1, 0) | vector(0, -1, 0) |
      | point(0, 1, 0.5) | vector(0, -1, 0) |
      | point(0, 2, 0)   | vector(0, 1, 0)  |
      | point(0.5, 2, 0) | vector(0, 1, 0)  |
      | point(0, 2, 0.5) | vector(0, 1, 0)  |