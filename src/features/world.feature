Feature: World

  Scenario: Creating a world
    Given world ← world()
    Then world contains no objects
    And world has no light source

  Scenario: The default world
    Given light ← point_light(point(-10, 10, -10), color(1, 1, 1))
    And shape1 ← sphere() with:
      | material.color    | (0.8, 1.0, 0.6) |
      | material.diffuse  | 0.7             |
      | material.specular | 0.2             |
    And shape2 ← sphere() with:
      | transform | scaling(0.5, 0.5, 0.5) |
    When world ← default_world()
    Then world.light = light
    And world contains shape1
    And world contains shape2

  Scenario: Intersect a world with a ray
    Given world ← default_world()
    And ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    When intersections ← intersect_world(world, ray)
    Then intersections.count = 4
    And intersections[0].t = 4
    And intersections[1].t = 4.5
    And intersections[2].t = 5.5
    And intersections[3].t = 6

  Scenario: Shading an intersection
    Given world ← default_world()
    And ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And shape ← the first object in world
    And intersection ← intersection(4, shape)
    When comps ← prepare_computations(intersection, ray)
    And color ← shade_hit(world, comps)
    Then color = color(0.38066, 0.47583, 0.2855)

  Scenario: Shading an intersection from the inside
    Given world ← default_world()
    And world.light ← point_light(point(0, 0.25, 0), color(1, 1, 1))
    And ray ← ray(point(0, 0, 0), vector(0, 0, 1))
    And shape ← the second object in world
    And intersection ← intersection(0.5, shape)
    When comps ← prepare_computations(intersection, ray)
    And color ← shade_hit(world, comps)
    Then color = color(0.90498, 0.90498, 0.90498)

  Scenario: The color when a ray misses
    Given world ← default_world()
    And ray ← ray(point(0, 0, -5), vector(0, 1, 0))
    When color ← color_at(world, ray)
    Then color = color(0, 0, 0)

  Scenario: The color when a ray hits
    Given world ← default_world()
    And ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    When color ← color_at(world, ray)
    Then color = color(0.38066, 0.47583, 0.2855)

  Scenario: The color with an intersection behind the ray
    Given world ← default_world()
    And outer ← the first object in world
    And outer.material.ambient ← 1
    And inner ← the second object in world
    And inner.material.ambient ← 1
    And ray ← ray(point(0, 0, 0.75), vector(0, 0, -1))
    When color ← color_at(world, ray)
    Then color = inner.material.color

  Scenario: There is no shadow when nothing is collinear with point and light
    Given world ← default_world()
    And point ← point(0, 10, 0)
    Then is_shadowed(world, point) is false

  Scenario: The shadow when an object is between the point and the light
    Given world ← default_world()
    And point ← point(10, -10, 10)
    Then is_shadowed(world, point) is true

  Scenario: There is no shadow when an object is behind the light
    Given world ← default_world()
    And point ← point(-20, 20, -20)
    Then is_shadowed(world, point) is false

  Scenario: There is no shadow when an object is behind the point
    Given world ← default_world()
    And point ← point(-2, 2, -2)
    Then is_shadowed(world, point) is false

  Scenario: shade_hit() is given an intersection in shadow
    Given world ← world()
    And world.light ← point_light(point(0, 0, -10), color(1, 1, 1))
    And shape1 ← sphere()
    And shape1 is added to world
    And shape2 ← sphere() with:
      | transform | translation(0, 0, 10) |
    And shape2 is added to world
    And ray ← ray(point(0, 0, 5), vector(0, 0, 1))
    And intersection ← intersection(4, shape2)
    When comps ← prepare_computations(intersection, ray)
    And color ← shade_hit(world, comps)
    Then color = color(0.1, 0.1, 0.1)

  Scenario: The reflected color for a nonreflective material
    Given world ← default_world()
    And ray ← ray(point(0, 0, 0), vector(0, 0, 1))
    And shape ← the second object in world
    And shape.material.ambient ← 1
    And intersection ← intersection(1, shape)
    When comps ← prepare_computations(intersection, ray)
    And color ← reflected_color(world, comps)
    Then color = color(0, 0, 0)

  Scenario: The reflected color for a reflective material
    Given world ← default_world()
    And shape ← plane() with:
      | material.reflective | 0.5                   |
      | transform           | translation(0, -1, 0) |
    And shape is added to world
    And ray ← ray(point(0, 0, -3), vector(0, -√2/2, √2/2))
    And intersection ← intersection(√2, shape)
    When comps ← prepare_computations(intersection, ray)
    And color ← reflected_color(world, comps)
    Then color = color(0.19032, 0.2379, 0.14274)

  Scenario: shade_hit() with a reflective material
    Given world ← default_world()
    And shape ← plane() with:
      | material.reflective | 0.5                   |
      | transform           | translation(0, -1, 0) |
    And shape is added to world
    And ray ← ray(point(0, 0, -3), vector(0, -√2/2, √2/2))
    And intersection ← intersection(√2, shape)
    When comps ← prepare_computations(intersection, ray)
    And color ← shade_hit(world, comps)
    Then color = color(0.87677, 0.92436, 0.82918)

  Scenario: color_at() with mutually reflective surfaces
    Given world ← world()
    And world.light ← point_light(point(0, 0, 0), color(1, 1, 1))
    And lower ← plane() with:
      | material.reflective | 1                     |
      | transform           | translation(0, -1, 0) |
    And lower is added to world
    And upper ← plane() with:
      | material.reflective | 1                    |
      | transform           | translation(0, 1, 0) |
    And upper is added to world
    And ray ← ray(point(0, 0, 0), vector(0, 1, 0))
    Then color_at(world, ray) should terminate successfully

  Scenario: The reflected color at the maximum recursive depth
    Given world ← default_world()
    And shape ← plane() with:
      | material.reflective | 0.5                   |
      | transform           | translation(0, -1, 0) |
    And shape is added to world
    And ray ← ray(point(0, 0, -3), vector(0, -√2/2, √2/2))
    And intersection ← intersection(√2, shape)
    When comps ← prepare_computations(intersection, ray)
    And color ← reflected_color(world, comps, 0)
    Then color = color(0, 0, 0)

  Scenario: The refracted color with an opaque surface
    Given world ← default_world()
    And shape ← the first object in world
    And ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And intersections ← intersections(4:shape, 6:shape)
    When comps ← prepare_computations(intersections[0], ray, intersections)
    And color ← refracted_color(world, comps, 5)
    Then color = color(0, 0, 0)

  Scenario: The refracted color at the maximum recursive depth
    Given world ← default_world()
    And shape ← the first object in world
    And shape has:
      | material.transparency     | 1.0 |
      | material.refractive_index | 1.5 |
    And ray ← ray(point(0, 0, -5), vector(0, 0, 1))
    And intersections ← intersections(4:shape, 6:shape)
    When comps ← prepare_computations(intersections[0], ray, intersections)
    And color ← refracted_color(world, comps, 0)
    Then color = color(0, 0, 0)

  Scenario: The refracted color under total internal reflection
    Given world ← default_world()
    And shape ← the first object in world
    And shape has:
      | material.transparency     | 1.0 |
      | material.refractive_index | 1.5 |
    And ray ← ray(point(0, 0, √2/2), vector(0, 1, 0))
    And intersections ← intersections(-√2/2:shape, √2/2:shape)
    # NOTE: this time you're inside the sphere, so you need
    # to look at the second intersection, intersections[1], not intersections[0]
    When comps ← prepare_computations(intersections[1], ray, intersections)
    And color ← refracted_color(world, comps, 5)
    Then color = color(0, 0, 0)

  Scenario: The refracted color with a refracted ray
    Given world ← default_world()
    And shape1 ← the first object in world
    And shape1 has:
      | material.ambient | 1.0            |
      | material.pattern | test_pattern() |
    And shape2 ← the second object in world
    And shape2 has:
      | material.transparency     | 1.0 |
      | material.refractive_index | 1.5 |
    And ray ← ray(point(0, 0, 0.1), vector(0, 1, 0))
    And intersections ← intersections(-0.9899:shape1, -0.4899:shape2, 0.4899:shape2, 0.9899:shape1)
    When comps ← prepare_computations(intersections[2], ray, intersections)
    And color ← refracted_color(world, comps, 5)
    Then color = color(0, 0.99888, 0.04725)

  Scenario: shade_hit() with a transparent material
    Given world ← default_world()
    And shape1 ← plane() with:
      | transform                 | translation(0, -1, 0) |
      | material.transparency     | 0.5                   |
      | material.refractive_index | 1.5                   |
    And shape1 is added to world
    And shape2 ← sphere() with:
      | material.color   | (1, 0, 0)                  |
      | material.ambient | 0.5                        |
      | transform        | translation(0, -3.5, -0.5) |
    And shape2 is added to world
    And ray ← ray(point(0, 0, -3), vector(0, -√2/2, √2/2))
    And intersections ← intersections(√2:shape1)
    When comps ← prepare_computations(intersections[0], ray, intersections)
    And color ← shade_hit(world, comps, 5)
    Then color = color(0.93642, 0.68642, 0.68642)

  Scenario: shade_hit() with a reflective, transparent material
    Given world ← default_world()
    And ray ← ray(point(0, 0, -3), vector(0, -√2/2, √2/2))
    And shape1 ← plane() with:
      | transform                 | translation(0, -1, 0) |
      | material.reflective       | 0.5                   |
      | material.transparency     | 0.5                   |
      | material.refractive_index | 1.5                   |
    And shape1 is added to world
    And shape2 ← sphere() with:
      | material.color   | (1, 0, 0)                  |
      | material.ambient | 0.5                        |
      | transform        | translation(0, -3.5, -0.5) |
    And shape2 is added to world
    And intersections ← intersections(√2:shape1)
    When comps ← prepare_computations(intersections[0], ray, intersections)
    And color ← shade_hit(world, comps, 5)
    Then color = color(0.93391, 0.69643, 0.69243)