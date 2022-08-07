Feature: Sampling Strategies

  Scenario: Sampling once at the center of the pixel
    Given world ← default_world()
    And camera ← camera(11, 11, π/2)
    And from ← point(0, 0, -5)
    And to ← point(0, 0, 0)
    And up ← vector(0, 1, 0)
    And camera.transform ← view_transform(from, to, up)
    Then color_at_pixel(camera, world, 5, 5) = linear_rgb_color(0.38066, 0.47583, 0.2855)

  Scenario Outline: Sampling at multiple points in the pixel
    Given world ← default_world()
    And camera ← camera(11, 11, π/2, multi(<sample count>))
    And from ← point(0, 0, -5)
    And to ← point(0, 0, 0)
    And up ← vector(0, 1, 0)
    And camera.transform ← view_transform(from, to, up)
    Then color_at_pixel(camera, world, 5, 5) = linear_rgb_color(<expected rgb>)
    Scenarios:
      | sample count | expected rgb                 |
      | 2            | 0.370546, 0.463182, 0.277910 |
      | 4            | 0.366851, 0.458563, 0.275138 |
      | 8            | 0.365553, 0.456941, 0.274164 |
      | 16           | 0.381912, 0.477320, 0.286505 |
