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
      0 0 0 0 0 0 0 188 0 0 0 0 0 0 0
      0 0 0 0 0 0 0 0 0 0 0 0 0 0 255
      """

  Scenario: Splitting long lines in PPM files
    Given canvas ← canvas(10, 2)
    When every pixel of canvas is set to color(1, 0.8, 0.6)
    And ppm ← canvas_to_ppm(canvas)
    Then lines 4-7 of ppm are
      """
      255 231 203 255 231 203 255 231 203 255 231 203 255 231 203 255 231
      203 255 231 203 255 231 203 255 231 203 255 231 203
      255 231 203 255 231 203 255 231 203 255 231 203 255 231 203 255 231
      203 255 231 203 255 231 203 255 231 203 255 231 203
      """

  Scenario: PPM files are terminated by a newline character
    Given canvas ← canvas(5, 3)
    When ppm ← canvas_to_ppm(canvas)
    Then ppm ends with a newline character

  Scenario: Reading a file with the wrong magic number
    Given ppm ← a file containing:
    """
    P32
    1 1
    255
    0 0 0
    """
    Then canvas_from_ppm(ppm) should fail

  Scenario: Reading a PPM returns a canvas of the right size
    Given ppm ← a file containing:
    """
    P3
    10 2
    255
    0 0 0  0 0 0  0 0 0  0 0 0  0 0 0
    0 0 0  0 0 0  0 0 0  0 0 0  0 0 0
    0 0 0  0 0 0  0 0 0  0 0 0  0 0 0
    0 0 0  0 0 0  0 0 0  0 0 0  0 0 0
    """
    When canvas ← canvas_from_ppm(ppm)
    Then canvas.width = 10
    And canvas.height = 2

  Scenario Outline: Reading pixel data from a PPM file
    Given ppm ← a file containing:
    """
    P3
    4 3
    255
    255 127 0  0 127 255  127 255 0  255 255 255
    0 0 0  255 0 0  0 255 0  0 0 255
    255 255 0  0 255 255  255 0 255  127 127 127
    """
    When canvas ← canvas_from_ppm(ppm)
    Then pixel_at(canvas, <x>, <y>) = <color>

    Examples:
      | x | y | color                         |
      | 0 | 0 | color(1, 0.2122, 0)           |
      | 1 | 0 | color(0, 0.2122, 1)           |
      | 2 | 0 | color(0.2122, 1, 0)           |
      | 3 | 0 | color(1, 1, 1)                |
      | 0 | 1 | color(0, 0, 0)                |
      | 1 | 1 | color(1, 0, 0)                |
      | 2 | 1 | color(0, 1, 0)                |
      | 3 | 1 | color(0, 0, 1)                |
      | 0 | 2 | color(1, 1, 0)                |
      | 1 | 2 | color(0, 1, 1)                |
      | 2 | 2 | color(1, 0, 1)                |
      | 3 | 2 | color(0.2122, 0.2122, 0.2122) |

  Scenario: PPM parsing ignores comment lines
    Given ppm ← a file containing:
    """
    P3
    # this is a comment
    2 1
    # this, too
    255
    # another comment
    255 255 255
    # oh, no, comments in the pixel data!
    255 0 255
    """
    When canvas ← canvas_from_ppm(ppm)
    Then pixel_at(canvas, 0, 0) = color(1, 1, 1)
    And pixel_at(canvas, 1, 0) = color(1, 0, 1)

  Scenario: PPM parsing allows an RGB triple to span lines
    Given ppm ← a file containing:
    """
    P3
    1 1
    255
    51
    153

    204
    """
    When canvas ← canvas_from_ppm(ppm)
    Then pixel_at(canvas, 0, 0) = color(0.0331, 0.3185, 0.6038)

  Scenario: PPM parsing respects the scale setting
    Given ppm ← a file containing:
    """
    P3
    2 2
    100
    100 100 100  50 50 50
    75 50 25  0 0 0
    """
    When canvas ← canvas_from_ppm(ppm)
    Then pixel_at(canvas, 0, 1) = color(0.5225, 0.2140, 0.0508)
