Feature: Camera

  Scenario: Constructing a camera
    Given hsize ← 160
    And vsize ← 120
    And field_of_view ← π/2
    When camera ← camera(hsize, vsize, field_of_view)
    Then camera.hsize = 160
    And camera.vsize = 120
    And camera.field_of_view = π/2
    And camera.transform = identity_matrix

  Scenario: The pixel size for a horizontal canvas
    Given camera ← camera(200, 125, π/2)
    Then camera.pixel_size = 0.01

  Scenario: The pixel size for a vertical canvas
    Given camera ← camera(125, 200, π/2)
    Then camera.pixel_size = 0.01

  Scenario: Constructing a ray through the center of the canvas
    Given camera ← camera(201, 101, π/2)
    When ray ← ray_for_pixel_offset(camera, 100.5, 50.5)
    Then ray.origin = point(0, 0, 0)
    And ray.direction = vector(0, 0, -1)

  Scenario: Constructing a ray through a corner of the canvas
    Given camera ← camera(201, 101, π/2)
    When ray ← ray_for_pixel_offset(camera, 0.5, 0.5)
    Then ray.origin = point(0, 0, 0)
    And ray.direction = vector(0.66519, 0.33259, -0.66851)

  Scenario: Constructing a ray when the camera is transformed
    Given camera ← camera(201, 101, π/2)
    When camera.transform ← rotation_y(π/4) * translation(0, -2, 5)
    And ray ← ray_for_pixel_offset(camera, 100.5, 50.5)
    Then ray.origin = point(0, 2, -5)
    And ray.direction = vector(√2/2, 0, -√2/2)

  Scenario: Rendering a world with a camera
    Given world ← default_world()
    And camera ← camera(11, 11, π/2)
    And from ← point(0, 0, -5)
    And to ← point(0, 0, 0)
    And up ← vector(0, 1, 0)
    And camera.transform ← view_transform(from, to, up)
    When image ← render(camera, world)
    Then pixel_at(image, 5, 5) = linear_rgb_color(0.38066, 0.47583, 0.2855)