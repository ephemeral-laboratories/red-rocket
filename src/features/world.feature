Feature: World

  Scenario: Creating a world
    Given w ← world()
    Then w contains no objects
    And w has no light source

  Scenario: The default world
    Given light ← point_light(point(-10, 10, -10), color(1, 1, 1))
    And s1 ← sphere() with:
      | material.color | (0.8, 1.0, 0.6) |
      | material.diffuse | 0.7 |
      | material.specular | 0.2 |
    And s2 ← sphere() with:
      | transform | scaling(0.5, 0.5, 0.5) |
    When w ← default_world()
    Then w.light = light
    And w contains s1
    And w contains s2

  Scenario: Intersect a world with a ray
    Given w ← default_world()
    And r ← ray(point(0, 0, -5), vector(0, 0, 1))
    When xs ← intersect_world(w, r)
    Then xs.count = 4
    And xs[0].t = 4
    And xs[1].t = 4.5
    And xs[2].t = 5.5
    And xs[3].t = 6

  Scenario: Shading an intersection
    Given w ← default_world()
    And r ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← the first object in w
    And i ← intersection(4, shape)
    When comps ← prepare_computations(i, r)
    And c ← shade_hit(w, comps)
    Then c = color(0.38066, 0.47583, 0.2855)

  Scenario: Shading an intersection from the inside
    Given w ← default_world()
    And w.light ← point_light(point(0, 0.25, 0), color(1, 1, 1))
    And r ← ray(point(0, 0, 0), vector(0, 0, 1))
    And shape ← the second object in w
    And i ← intersection(0.5, shape)
    When comps ← prepare_computations(i, r)
    And c ← shade_hit(w, comps)
    Then c = color(0.90498, 0.90498, 0.90498)

  Scenario: The color when a ray misses
    Given w ← default_world()
    And r ← ray(point(0, 0, -5), vector(0, 1, 0))
    When c ← color_at(w, r)
    Then c = color(0, 0, 0)

  Scenario: The color when a ray hits
    Given w ← default_world()
    And r ← ray(point(0, 0, -5), vector(0, 0, 1))
    When c ← color_at(w, r)
    Then c = color(0.38066, 0.47583, 0.2855)

  Scenario: The color with an intersection behind the ray
    Given w ← default_world()
    And outer ← the first object in w
    And outer.material.ambient ← 1
    And inner ← the second object in w
    And inner.material.ambient ← 1
    And r ← ray(point(0, 0, 0.75), vector(0, 0, -1))
    When c ← color_at(w, r)
    Then c = inner.material.color