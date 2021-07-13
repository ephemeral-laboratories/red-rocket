Feature: Materials

  Background:
    Given material ← material()
    And position ← point(0, 0, 0)

  Scenario: The default material
    Given material ← material()
    Then material.color = linear_rgb_color(1, 1, 1)
    And material.ambient = 0.1
    And material.diffuse = 0.9
    And material.specular = 0.9
    And material.shininess = 200

  Scenario: Lighting with the eye between the light and the surface
    Given eyeline ← vector(0, 0, -1)
    And normal ← vector(0, 0, -1)
    And light ← point_light(point(0, 0, -10), linear_rgb_color(1, 1, 1))
    When result ← lighting(material, light, position, eyeline, normal)
    Then result = linear_rgb_color(1.9, 1.9, 1.9)

  Scenario: Lighting with the eye between light and surface, eye offset 45°
    Given eyeline ← vector(0, √2/2, -√2/2)
    And normal ← vector(0, 0, -1)
    And light ← point_light(point(0, 0, -10), linear_rgb_color(1, 1, 1))
    When result ← lighting(material, light, position, eyeline, normal)
    Then result = linear_rgb_color(1, 1, 1)

  Scenario: Lighting with eye opposite surface, light offset 45°
    Given eyeline ← vector(0, 0, -1)
    And normal ← vector(0, 0, -1)
    And light ← point_light(point(0, 10, -10), linear_rgb_color(1, 1, 1))
    When result ← lighting(material, light, position, eyeline, normal)
    Then result = linear_rgb_color(0.7364, 0.7364, 0.7364)

  Scenario: Lighting with eye in the path of the reflection vector
    Given eyeline ← vector(0, -√2/2, -√2/2)
    And normal ← vector(0, 0, -1)
    And light ← point_light(point(0, 10, -10), linear_rgb_color(1, 1, 1))
    When result ← lighting(material, light, position, eyeline, normal)
    Then result = linear_rgb_color(1.6364, 1.6364, 1.6364)

  Scenario: Lighting with the light behind the surface
    Given eyeline ← vector(0, 0, -1)
    And normal ← vector(0, 0, -1)
    And light ← point_light(point(0, 0, 10), linear_rgb_color(1, 1, 1))
    When result ← lighting(material, light, position, eyeline, normal)
    Then result = linear_rgb_color(0.1, 0.1, 0.1)

  Scenario: Lighting with the surface in shadow
    Given eyeline ← vector(0, 0, -1)
    And normal ← vector(0, 0, -1)
    And light ← point_light(point(0, 0, -10), linear_rgb_color(1, 1, 1))
    And in_shadow ← true
    When result ← lighting(material, light, position, eyeline, normal, in_shadow)
    Then result = linear_rgb_color(0.1, 0.1, 0.1)

  Scenario: Lighting with a pattern applied
    Given material.pattern ← stripe_pattern(linear_rgb_color(1, 1, 1), linear_rgb_color(0, 0, 0))
    And material.ambient ← 1
    And material.diffuse ← 0
    And material.specular ← 0
    And eyeline ← vector(0, 0, -1)
    And normal ← vector(0, 0, -1)
    And light ← point_light(point(0, 0, -10), linear_rgb_color(1, 1, 1))
    When color1 ← lighting(material, light, point(0.9, 0, 0), eyeline, normal, false)
    And color2 ← lighting(material, light, point(1.1, 0, 0), eyeline, normal, false)
    Then color1 = linear_rgb_color(1, 1, 1)
    And color2 = linear_rgb_color(0, 0, 0)

  Scenario: Reflectivity for the default material
    Given material ← material()
    Then material.reflective = 0

  Scenario: Transparency and Refractive Index for the default material
    Given material ← material()
    Then material.transparency = 0
    And material.refractive_index = 1

  Scenario: A helper for producing a sphere with a glassy material
    Given shape ← glass_sphere()
    Then shape.transform = identity_matrix
    And shape.material.transparency = 1
    And shape.material.refractive_index = 1.5