Feature: Canvas

  Scenario: Creating a canvas
    Given canvas ← canvas(10, 20)
    Then canvas.width = 10
    And canvas.height = 20
    And every pixel of canvas is color(0, 0, 0)

  Scenario: Writing pixels to a canvas
    Given canvas ← canvas(10, 20)
    And red ← color(1, 0, 0)
    When write_pixel(canvas, 2, 3, red)
    Then pixel_at(canvas, 2, 3) = red

  Scenario: Constructing the PPM header
    Given canvas ← canvas(5, 3)
    When ppm ← canvas_to_ppm(canvas)
    Then lines 1-3 of ppm are
      """
      P3
      5 3
      255
      """

  Scenario: Constructing the PPM pixel data
    Given canvas ← canvas(5, 3)
    And color1 ← color(1.5, 0, 0)
    And color2 ← color(0, 0.5, 0)
    And color3 ← color(-0.5, 0, 1)
    When write_pixel(canvas, 0, 0, color1)
    And write_pixel(canvas, 2, 1, color2)
    And write_pixel(canvas, 4, 2, color3)
    And ppm ← canvas_to_ppm(canvas)
    Then lines 4-6 of ppm are
      """
      255 0 0 0 0 0 0 0 0 0 0 0 0 0 0
      0 0 0 0 0 0 0 128 0 0 0 0 0 0 0
      0 0 0 0 0 0 0 0 0 0 0 0 0 0 255
      """

  Scenario: Splitting long lines in PPM files
    Given canvas ← canvas(10, 2)
    When every pixel of canvas is set to color(1, 0.8, 0.6)
    And ppm ← canvas_to_ppm(canvas)
    Then lines 4-7 of ppm are
      """
      255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204
      153 255 204 153 255 204 153 255 204 153 255 204 153
      255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204
      153 255 204 153 255 204 153 255 204 153 255 204 153
      """

  Scenario: PPM files are terminated by a newline character
    Given canvas ← canvas(5, 3)
    When ppm ← canvas_to_ppm(canvas)
    Then ppm ends with a newline character
