Feature: OBJ File

  Scenario: Ignoring unrecognized lines
    Given gibberish ← a file containing:
      """
      There was a young lady named Bright
      who traveled much faster than light.
      She set out one day
      in a relative way,
      and came back the previous night.
      """
    When parser ← parse_obj_file(gibberish)
    Then parser should have ignored 5 lines

  Scenario: Vertex records
    Given file ← a file containing:
      """
      v -1 1 0
      v -1.0000 0.5000 0.0000
      v 1 0 0
      v 1 1 0
      """
    When parser ← parse_obj_file(file)
    Then parser.vertices[1] = point(-1, 1, 0)
    And parser.vertices[2] = point(-1, 0.5, 0)
    And parser.vertices[3] = point(1, 0, 0)
    And parser.vertices[4] = point(1, 1, 0)

  Scenario: Parsing triangle faces
    Given file ← a file containing:
      """
      v -1 1 0
      v -1 0 0
      v 1 0 0
      v 1 1 0
      f 1 2 3
      f 1 3 4
      """
    When parser ← parse_obj_file(file)
    And group ← parser.default_group
    And shape1 ← first child of group
    And shape2 ← second child of group
    Then shape1.point1 = parser.vertices[1]
    And shape1.point2 = parser.vertices[2]
    And shape1.point3 = parser.vertices[3]
    And shape2.point1 = parser.vertices[1]
    And shape2.point2 = parser.vertices[3]
    And shape2.point3 = parser.vertices[4]

  Scenario: Triangulating polygons
    Given file ← a file containing:
      """
      v -1 1 0
      v -1 0 0
      v 1 0 0
      v 1 1 0
      v 0 2 0
      f 1 2 3 4 5
      """
    When parser ← parse_obj_file(file)
    And group ← parser.default_group
    And shape1 ← first child of group
    And shape2 ← second child of group
    And shape3 ← third child of group
    Then shape1.point1 = parser.vertices[1]
    And shape1.point2 = parser.vertices[2]
    And shape1.point3 = parser.vertices[3]
    And shape2.point1 = parser.vertices[1]
    And shape2.point2 = parser.vertices[3]
    And shape2.point3 = parser.vertices[4]
    And shape3.point1 = parser.vertices[1]
    And shape3.point2 = parser.vertices[4]
    And shape3.point3 = parser.vertices[5]

  Scenario: Triangles in groups
    Given file ← the file "triangles.obj"
    When parser ← parse_obj_file(file)
    And group1 ← "FirstGroup" from parser
    And group2 ← "SecondGroup" from parser
    And shape1 ← first child of group1
    And shape2 ← first child of group2
    Then shape1.point1 = parser.vertices[1]
    And shape1.point2 = parser.vertices[2]
    And shape1.point3 = parser.vertices[3]
    And shape2.point1 = parser.vertices[1]
    And shape2.point2 = parser.vertices[3]
    And shape2.point3 = parser.vertices[4]

  Scenario: Converting an OBJ file to a group
    Given file ← the file "triangles.obj"
    And parser ← parse_obj_file(file)
    When group ← obj_to_group(parser)
    Then group includes "FirstGroup" from parser
    And group includes "SecondGroup" from parser

  Scenario: Vertex normal records
    Given file ← a file containing:
      """
      vn 0 0 1
      vn 0.707 0 -0.707
      vn 1 2 3
      """
    When parser ← parse_obj_file(file)
    Then parser.normals[1] = vector(0, 0, 1)
    And parser.normals[2] = vector(0.707, 0, -0.707)
    And parser.normals[3] = vector(1, 2, 3)

  Scenario: Faces with normals
    Given file ← a file containing:
      """
      v 0 1 0
      v -1 0 0
      v 1 0 0
      vn -1 0 0
      vn 1 0 0
      vn 0 1 0
      f 1//3 2//1 3//2
      f 1/0/3 2/102/1 3/14/2
      """
    When parser ← parse_obj_file(file)
    And group ← parser.default_group
    And shape1 ← first child of group
    And shape2 ← second child of group
    Then shape1.point1 = parser.vertices[1]
    And shape1.point2 = parser.vertices[2]
    And shape1.point3 = parser.vertices[3]
    And shape1.normal1 = parser.normals[3]
    And shape1.normal2 = parser.normals[1]
    And shape1.normal3 = parser.normals[2]
    And shape2 = shape1

  Scenario: Faces with materials
    Given mtl_file ← a file named 'thing.mtl' containing:
      """
      newmtl Red
      Kd 1 0 0
      newmtl Blue
      Kd 0 0 1
      """
    And obj_file ← a file named 'thing.obj' containing:
      """
      mtllib thing.mtl
      v 0 1 0
      v -1 0 0
      v 1 0 0
      vn -1 0 0
      vn 1 0 0
      vn 0 1 0
      usemtl Red
      f 1//3 2//1 3//2
      usemtl Blue
      f 1/0/3 2/102/1 3/14/2
      """
    When parser ← parse_obj_file(obj_file)
    And group ← parser.default_group
    And shape1 ← first child of group
    And shape2 ← second child of group
    Then shape1.material.diffuse = linear_rgb_color(1, 0, 0)
    And shape2.material.diffuse = linear_rgb_color(0, 0, 1)