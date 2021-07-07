Feature: Material Library (MTL) File

  Scenario: A material library contains more than one material
    Given file ← a file containing:
      """
      newmtl my_red
      newmtl my_blue
      newmtl my_green

      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 3
