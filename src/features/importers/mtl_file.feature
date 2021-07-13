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

  # TODO: Support everything in here
#  Scenario: A material library is defined relatively comprehensively
#    Given file ← a file containing:
#      """
#      # Straight from the format docs at http://www.fileformat.info/format/material/
#      newmtl my_mtl
#
#      # Material color and illumination statements:
#      Ka 0.0435 0.0435 0.0435
#      Kd 0.1086 0.1086 0.1086
#      Ks 0.0000 0.0000 0.0000
#      Tf 0.9885 0.9885 0.9885
#      illum 6
#      d -halo 0.6600
#      Ns 10.0000
#      sharpness 60
#      Ni 1.19713
#
#      # Texture map statements:
#      map_Ka -s 1 1 1 -o 0 0 0 -mm 0 1 chrome.mpc
#      map_Kd -s 1 1 1 -o 0 0 0 -mm 0 1 chrome.mpc
#      map_Ks -s 1 1 1 -o 0 0 0 -mm 0 1 chrome.mpc
#      map_Ns -s 1 1 1 -o 0 0 0 -mm 0 1 wisp.mps
#      map_d -s 1 1 1 -o 0 0 0 -mm 0 1 wisp.mps
#      disp -s 1 1 .5 wisp.mps
#      decal -s 1 1 1 -o 0 0 0 -mm 0 1 sand.mps
#      bump -s 1 1 1 -o 0 0 0 -bm 1 sand.mpb
#
#      # Reflection map statement:
#      refl -type sphere -mm 0 1 clouds.mpc
#      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1

  Scenario: Names cannot include blanks
    Given file ← a file containing:
      """
      newmtl illegal name
      """
    Then parse_mtl_file(file) should fail

  Scenario: A material specifies the ambient reflectivity using RGB values
    Given file ← a file containing:
      """
      newmtl invar
      Ka 1.0 0.0 1.0
      """
    When parser ← parse_mtl_file(file)
    Then material.ambient = linear_rgb_color(1, 0, 1)

  Scenario: A material specifies the ambient reflectivity using only the R value
    Given file ← a file containing:
      """
      newmtl invar
      Ka 0.5
      """
    When parser ← parse_mtl_file(file)
    Then material.ambient = linear_rgb_color(0.5, 0.5, 0.5)

  Scenario: A material specifies the ambient reflectivity using CIEXYZ values
    Given file ← a file containing:
      """
      newmtl invar
      Ka xyz 0.4 0.7 0.3
      """
    When parser ← parse_mtl_file(file)
    Then material.ambient = cie_xyz_color(0.4, 0.7, 0.3)

  Scenario: A material specifies the ambient reflectivity using CIEXYZ values but only the X value
    Given file ← a file containing:
      """
      newmtl invar
      Ka xyz 0.4
      """
    When parser ← parse_mtl_file(file)
    Then material.ambient = cie_xyz_color(0.4, 0.4, 0.4)

  # TODO: RFL file support
  @wip
  Scenario: A material specifies the ambient reflectivity using an RFL file
    Given file ← a file containing:
      """
      newmtl invar
      Ka spectral file.rfl factor
      # "file.rfl" is the name of the .rfl file.
      # "factor" is an optional argument.
      # "factor" is a multiplier for the values in the .rfl file and defaults to 1.0, if not specified.
      """

  Scenario: A material specifies the diffuse reflectivity using RGB values
    Given file ← a file containing:
      """
      newmtl invar
      Kd 1.0 0.0 1.0
      """
    When parser ← parse_mtl_file(file)
    Then material.diffuse = linear_rgb_color(1.0, 0.0, 1.0)

  Scenario: A material specifies the diffuse reflectivity using CIEXYZ values
    Given file ← a file containing:
      """
      newmtl invar
      Kd xyz 0.4 0.7 0.3
      """
    When parser ← parse_mtl_file(file)
    Then material.diffuse = cie_xyz_color(0.4, 0.7, 0.3)

  # TODO: RFL file support
  @wip
  Scenario: A material specifies the diffuse reflectivity using an RFL file
    Given file ← a file containing:
      """
      newmtl invar
      Kd spectral file.rfl factor
      """

  Scenario: A material specifies the specular reflectivity using RGB values
    Given file ← a file containing:
      """
      newmtl invar
      Ks 1.0 0.0 1.0
      """
    When parser ← parse_mtl_file(file)
    Then material.specular = linear_rgb_color(1.0, 0.0, 1.0)

  Scenario: A material specifies the specular reflectivity using CIEXYZ values
    Given file ← a file containing:
      """
      newmtl invar
      Ks xyz 0.4 0.7 0.3
      """
    When parser ← parse_mtl_file(file)
    Then material.specular = cie_xyz_color(0.4, 0.7, 0.3)

  # TODO: RFL file support
  @wip
  Scenario: A material specifies the specular reflectivity using an RFL file
    Given file ← a file containing:
      """
      newmtl invar
      Ks spectral file.rfl factor
      """

  Scenario: A material specifies the transmission filter using RGB values
    Given file ← a file containing:
      """
      newmtl invar
      Tf 1.0 0.0 1.0
      """
    When parser ← parse_mtl_file(file)
    Then material.transparency = linear_rgb_color(1.0, 0.0, 1.0)

  Scenario: A material specifies the transmission filter using CIEXYZ values
    Given file ← a file containing:
      """
      newmtl invar
      Tf xyz 0.4 0.7 0.3
      """
    When parser ← parse_mtl_file(file)
    Then material.transparency = cie_xyz_color(0.4, 0.7, 0.3)

  # TODO: RFL file support
  @wip
  Scenario: A material specifies the transmission filter using an RFL file
    Given file ← a file containing:
      """
      newmtl invar
      Tf spectral file.rfl factor
      """

  Scenario: A material specifies the emission using RGB values [non-standard extension]
    Given file ← a file containing:
      """
      newmtl invar
      Ke 1.0 0.0 1.0
      """
    When parser ← parse_mtl_file(file)
    Then material.emission = linear_rgb_color(1.0, 0.0, 1.0)

  Scenario: A material specifies the emission using CIEXYZ values [non-standard extension]
    Given file ← a file containing:
      """
      newmtl invar
      Ke xyz 0.4 0.7 0.3
      """
    When parser ← parse_mtl_file(file)
    Then material.emission = cie_xyz_color(0.4, 0.7, 0.3)

  # TODO: RFL file support
  @wip
  Scenario: A material specifies the emission using an RFL file [non-standard extension]
    Given file ← a file containing:
      """
      newmtl invar
      Ke spectral file.rfl factor
      """

  ### TODO MORE SCENARIOS ###

  Scenario: A material specifies the illumination model
    Given file ← a file containing:
      """
      newmtl invar
      illum 3
      """
    When parser ← parse_mtl_file(file)
    Then material.illumination_model = 3

  Scenario: A material specifies the dissolve factor
    Given file ← a file containing:
      """
      newmtl invar
      d 0.75
      """
    When parser ← parse_mtl_file(file)
    Then material.dissolve = 0.75

  # TODO: Dissolve halo support
  #   d -halo factor
  #     specifies that a dissolve is dependent on the surface orientation relative to the viewer.
  #     For example, a sphere with the following dissolve, d -halo 0.0, will be fully dissolved at
  #     its center and will appear gradually more opaque toward its edge.
  #     "factor" is the minimum amount of dissolve applied to the material. The amount of dissolve will vary
  #     between 1.0 (fully opaque) and the specified "factor". The formula is:
  #       dissolve = 1.0 - (N*v)(1.0-factor)

  Scenario: A material specifies the specular exponent
    Given file ← a file containing:
      """
      newmtl invar
      Ns 10
      """
    When parser ← parse_mtl_file(file)
    Then material.shininess = 10

  # TODO: Reflection sharpness:
  #   sharpness value
  #     0-1000, default 60, note that values greater than 100 introduce artifacts?

  Scenario: A material specifies the index of refraction
    Given file ← a file containing:
      """
      newmtl invar
      Ni 1.2
      """
    When parser ← parse_mtl_file(file)
    Then material.refractive_index = 1.2

  # TODO: Texture map support
  @wip
  Scenario: A material specifies an ambient texture map
    Given file ← a file containing:
      """
      newmtl invar
      map_Ka ambient_map.png
      """

  # TODO: Texture map support
  @wip
  Scenario: A material specifies a diffuse texture map
    Given file ← a file containing:
      """
      newmtl invar
      map_Kd diffuse_map.png
      """

  # TODO: Texture map support
  @wip
  Scenario: A material specifies a specular texture map
    Given file ← a file containing:
      """
      newmtl invar
      map_Ks specular_map.png
      """

  # TODO: Texture map support
  @wip
  Scenario: A material specifies a specular exponent texture map
    Given file ← a file containing:
      """
      newmtl invar
      map_Ns specular_exponent_map.png
      """

  # TODO: Texture map support
  @wip
  Scenario: A material specifies a dissolve texture map
    Given file ← a file containing:
      """
      newmtl invar
      map_d dissolve_map.png
      """

  # TODO: Texture map support
  @wip
  Scenario: A material has anti-aliasing of its textures enabled
    Given file ← a file containing:
      """
      newmtl invar
      map_aat on
      """

  # TODO: Texture map support
  @wip
  Scenario: A material specifies a decal texture map
    Given file ← a file containing:
      """
      newmtl invar
      decal decal.png
      """
    # Then the decal is applied according to the following incredibly ambiguous formula:
    #   During rendering, the Ka, Kd, and Ks values and the map_Ka, map_Kd, and map_Ks values are blended according to the following formula:
    #   result_color=tex_color(tv)*decal(tv)+mtl_color*(1.0-decal(tv))
    #   where tv is the texture vertex.

  # Displacement map
  #   disp -options args filename

  # Bump map
  #   bump -options args filename

  # Reflection map
  #   refl -type sphere -options -args filename
  #     Specifies an infinitely large sphere that casts reflections onto the material. You specify one texture file.
  #   refl -type cube_side -options -args filenames
  #   refl -type cube_top -options -args filename
  #   refl -type cube_bottom -options -args filename
  #   refl -type cube_front -options -args filename
  #   refl -type cube_back -options -args filename
  #   refl -type cube_left -options -args filename
  #   refl -type cube_right -options -args filename

  # Common options for texture maps, applies to: map_Ka map_Kd map_Ks map_Ns map_d decal disp bump refl
  #   -blendu on | off
  #     The -blendu option turns texture blending in the horizontal direction (u direction) on or off. The default is on.
  #   -blendv on | off
  #     The -blendv option turns texture blending in the vertical direction (v direction) on or off. The default is on.
  #   -bm mult
  #     specifies a bump multiplier
  #   -boost value
  #     The -boost option increases the sharpness, or clarity, of mip-mapped texture files
  #   -cc on | off
  #     turns on color correction for the texture.
  #     You can use it only with the color map statements: map_Ka, map_Kd, and map_Ks.
  #   -clamp on | off
  #     The -clamp option turns clamping on or off. When clamping is on,
  #     textures are restricted to 0-1 in the uvw range. The default is off. off = tile
  #   -imfchan r | g | b | m | l | z
  #     specifies the channel used to create a scalar or bump texture. Scalar textures are applied to:
  #       - transparency
  #       - specular exponent
  #       - decal
  #       - displacement
  #       r specifies the red channel.
  #       g specifies the green channel.
  #       b specifies the blue channel.
  #       m specifies the matte channel.
  #       l specifies the luminance channel.
  #       z specifies the z-depth channel.
  #       The default for bump and scalar textures is "l" (luminance), unless you are building a decal.
  #       In that case, the default is "m" (matte).
  #   -mm base gain
  #     "base" adds a base value to the texture values. A positive value makes everything brighter;
  #       a negative value makes everything dimmer. The default is 0; the range is unlimited.
  #     "gain" expands the range of the texture values. Increasing the number increases the contrast.
  #       The default is 1; the range is unlimited.
  #   -o u v w
  #     offsets the position of the texture map on the surface by shifting the position of the map origin
  #   -s u v w
  #     scales the size of the texture pattern on the textured surface by expanding or shrinking the pattern
  #   -t u v w
  #     applies "turbulence" to the texture
  #   -texres resolution
  #     specifies the resolution of texture created when an image is used

  Scenario: Example 1. Neon green
  This is a bright green material. When applied to an object, it will remain bright green regardless of any lighting in the scene.
    Given file ← a file containing:
      """
      newmtl neon_green
      Kd 0.0000 1.0000 0.0000
      illum 0
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 2. Flat green
  This is a flat green material.
    Given file ← a file containing:
      """
      newmtl flat_green
      Ka 0.0000 1.0000 0.0000
      Kd 0.0000 1.0000 0.0000
      illum 1
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 3. Dissolved green
  This is a flat green, partially dissolved material.
    Given file ← a file containing:
      """
      newmtl diss_green
      Ka 0.0000 1.0000 0.0000
      Kd 0.0000 1.0000 0.0000
      d 0.8000
      illum 1
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 4. Shiny green
  This is a shiny green material. When applied to an object, it shows a white specular highlight.
    Given file ← a file containing:
      """
      newmtl shiny_green
      Ka 0.0000 1.0000 0.0000
      Kd 0.0000 1.0000 0.0000
      Ks 1.0000 1.0000 1.0000
      Ns 200.0000
      illum 1
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 5. Green mirror
  This is a reflective green material. When applied to an object, it reflects other objects in the same scene.
    Given file ← a file containing:
      """
      newmtl green_mirror
      Ka 0.0000 1.0000 0.0000
      Kd 0.0000 1.0000 0.0000
      Ks 0.0000 1.0000 0.0000
      Ns 200.0000
      illum 3
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 6. Fake windshield
  This material approximates a glass surface. Is it almost completely transparent, but it shows reflections
  of other objects in the scene. It will not distort the image of objects seen through the material.
    Given file ← a file containing:
      """
      newmtl fake_windsh
      Ka 0.0000 0.0000 0.0000
      Kd 0.0000 0.0000 0.0000
      Ks 0.9000 0.9000 0.9000
      d 0.1000
      Ns 200
      illum 4
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 7. Fresnel blue
  This material exhibits an effect known as Fresnel reflection. When applied to an object, white fringes may
  appear where the object's surface is viewed at a glancing angle.
    Given file ← a file containing:
      """
      newmtl fresnel_blu
      Ka 0.0000 0.0000 0.0000
      Kd 0.0000 0.0000 0.0000
      Ks 0.6180 0.8760 0.1430
      Ns 200
      illum 5
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 8. Real windshield
  This material accurately represents a glass surface. It filters of colorizes objects that are seen through it.
  Filtering is done according to the transmission color of the material. The material also distorts the image of
  objects according to its optical density. Note that the material is not dissolved and that its ambient, diffuse,
  and specular reflective colors have been set to black. Only the transmission color is non-black.
    Given file ← a file containing:
      """
      newmtl real_windsh
      Ka 0.0000 0.0000 0.0000
      Kd 0.0000 0.0000 0.0000
      Ks 0.0000 0.0000 0.0000
      Tf 1.0000 1.0000 1.0000
      Ns 200
      Ni 1.2000
      illum 6
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  Scenario: Example 9. Fresnel windshield
  This material combines the effects in examples 7 and 8.
    Given file ← a file containing:
      """
      newmtl fresnel_win
      Ka 0.0000 0.0000 1.0000
      Kd 0.0000 0.0000 1.0000
      Ks 0.6180 0.8760 0.1430
      Tf 1.0000 1.0000 1.0000
      Ns 200
      Ni 1.2000
      illum 7
      """
    When parser ← parse_mtl_file(file)
    Then parser.materials.size = 1

  # TODO: Spectral curve support
  @wip
  Scenario: Example 10. Tin
  This material is based on spectral reflectance samples taken from an actual piece of tin. These samples are
  stored in a separate .rfl file that is referred to by name in the material. Spectral sample files (.rfl) can
  be used in any type of material as an alternative to RGB values.
    Given file ← a file containing:
      """
      newmtl tin
      Ka spectral tin.rfl
      Kd spectral tin.rfl
      Ks spectral tin.rfl
      Ns 200
      illum 3
      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1

  # TODO: Spectral curve support
  @wip
  Scenario: Example 11. Pine Wood
  This material includes a texture map of a pine pattern. The material color is set to "ident" to preserve the
  texture's true color. When applied to an object, this texture map will affect only the ambient and diffuse
  regions of that object's surface.
  The color information for the texture is stored in a separate .mpc file that is referred to in the material
  by its name, "pine.mpc". If you use different .mpc files for ambient and diffuse, you will get unrealistic results.
    Given file ← a file containing:
      """
      newmtl pine_wood
      Ka spectral ident.rfl 1
      Kd spectral ident.rfl 1
      illum 1
      map_Ka pine.mpc
      map_Kd pine.mpc
      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1

  # TODO: Spectral curve support, bump map support
  @wip
  Scenario: Example 12. Bumpy leather
  This material includes a texture map of a leather pattern. The material color is set to "ident" to preserve the
  texture's true color. When applied to an object, it affects both the color of the object's surface and its
  apparent bumpiness.
  The color information for the texture is stored in a separate .mpc file that is referred to in the material by its
  name, "brown.mpc". The bump information is stored in a separate .mpb file that is referred to in the material by
  its name, "leath.mpb". The -bm option is used to raise the apparent height of the leather bumps.
    Given file ← a file containing:
      """
      newmtl bumpy_leath
      Ka spectral ident.rfl 1
      Kd spectral ident.rfl 1
      Ks spectral ident.rfl 1
      illum 2
      map_Ka brown.mpc
      map_Kd brown.mpc
      map_Ks brown.mpc
      bump -bm 2.000 leath.mpb
      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1

  # TODO: Texture map support
  @wip
  Scenario: Example 13. Frosted window
  This material includes a texture map used to alter the opacity of an object's surface. The material color is set
  to "ident" to preserve the texture's true color. When applied to an object, the object becomes transparent in
  certain areas and opaque in others.
  The variation between opaque and transparent regions is controlled by scalar information stored in a separate
  .mps file that is referred to in the material by its name, "window.mps". The "-mm" option is used to shift and
  compress the range of opacity.
    Given file ← a file containing:
      """
      newmtl frost_wind
      Ka 0.2 0.2 0.2
      Kd 0.6 0.6 0.6
      Ks 0.1 0.1 0.1
      d 1
      Ns 200
      illum 2
      map_d -mm 0.200 0.800 window.mps
      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1

  # TODO: Texture map support
  @wip
  Scenario: Example 14. Shifted logo
  This material includes a texture map which illustrates how a texture's origin may be shifted left/right
  (the "u" direction) or up/down (the "v" direction). The material color is set to "ident" to preserve the
  texture's true color.
  In this example, the original image of the logo is off-center to the left. To compensate, the texture's origin
  is shifted back to the right (the positive "u" direction) using the "-o" option to modify the origin.
    Given file ← a file containing:
      """
      newmtl shifted_logo
      Ka spectral ident.rfl 1
      Kd spectral ident.rfl 1
      Ks spectral ident.rfl 1
      illum 2
      map_Ka -o 0.200 0.000 0.000 logo.mpc
      map_Kd -o 0.200 0.000 0.000 logo.mpc
      map_Ks -o 0.200 0.000 0.000 logo.mpc
      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1

  # TODO: Texture map support
  @wip
  Scenario: Example 15. Scaled logo
  This material includes a texture map showing how a texture may be scaled left or right (in the "u" direction)
  or up and down (in the "v" direction). The material color is set to "ident" to preserve the texture's true color.
  In this example, the original image of the logo is too small. To compensate, the texture is scaled slightly to the
  right (in the positive "u" direction) and up (in the positive "v" direction) using the "-s" option to modify the
  scale.
    Given file ← a file containing:
      """
      newmtl scaled_logo
      Ka spectral ident.rfl 1
      Kd spectral ident.rfl 1
      Ks spectral ident.rfl 1
      illum 2
      map_Ka -s 1.200 1.200 0.000 logo.mpc
      map_Kd -s 1.200 1.200 0.000 logo.mpc
      map_Ks -s 1.200 1.200 0.000 logo.mpc
      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1

  # TODO: Reflection map support
  @wip
  Scenario: Example 16. Chrome with spherical reflection map
  This illustrates a common use for local reflection maps (defined in a material).
  This material is highly reflective with no diffuse or ambient contribution. Its reflection map is an image
  with silver streaks that yields a chrome appearance when viewed as a reflection.
    Given file ← a file containing:
        """
        newmtl chrome
        Ka 0 0 0
        Kd 0 0 0
        Ks .7 .7 .7
        illum 1
        refl -type sphere chrome.rla
      """
#    When parser ← parse_mtl_file(file)
#    Then parser.materials.size = 1


# TODO: Tip These statements (RGB vs CIEXYZ vs spectral) are mutually exclusive.
#       They cannot be used concurrently in the same material.


#
#sharpness value
#
#Specifies the sharpness of the reflections from the local reflection map. If a material does not have a local reflection map defined in its material definition, sharpness will apply to the global reflection map defined in PreView.
#
#"value" can be a number from 0 to 1000. The default is 60. A high value results in a clear reflection of objects in the reflection map.
#
#Tip Sharpness values greater than 100 map introduce aliasing effects in flat surfaces that are viewed at a sharp angle
#

#
#Material texture map
#
#Texture map statements modify the material parameters of a surface by associating an image or texture file with material parameters that can be mapped. By modifying existing parameters instead of replacing them, texture maps provide great flexibility in changing the appearance of an object's surface.
#
#Image files and texture files can be used interchangeably. If you use an image file, that file is converted to a texture in memory and is discarded after rendering.
#
#Tip Using images instead of textures saves disk space and setup time, however, it introduces a small computational cost at the beginning of a render.
#
#The material parameters that can be modified by a texture map are:
#
#- Ka (color)
#- Kd (color)
#- Ks (color)
#- Ns (scalar)
#- d (scalar)
#
#In addition to the material parameters, the surface normal can be modified.
#
#
# Image file types
#
# You can link any image file type that is currently supported. Supported image file types are listed in the chapter
# "About Image" in the "Advanced Visualizer User's Guide". You can also use the "im_info -a" command to list Image
# file types, among other things.
#
#
# Texture file types
#
# The texture file types you can use are:
# - mip-mapped texture files (.mpc, .mps, .mpb)
# - compiled procedural texture files (.cxc, .cxs, .cxb)

# Mip-mapped texture files
#
# Mip-mapped texture files are created from images using the Create Textures panel in the Director or the
# "texture2D" program. There are three types of texture files:
#
# - color texture files (.mpc)
# - scalar texture files (.mps)
# - bump texture files (.mpb)
#
#Color textures. Color texture files are designated by an extension of ".mpc" in the filename, such as "chrome.mpc". Color textures modify the material color as follows:
#
#- Ka - material ambient is multiplied by the texture value
#- Kd - material diffuse is multiplied by the texture value
#- Ks - material specular is multiplied by the texture value
#
#Scalar textures. Scalar texture files are designated by an extension of ".mps" in the filename, such as "wisp.mps". Scalar textures modify the material scalar values as follows:
#
#Bump textures. Bump texture files are designated by an extension of ".mpb" in the filename, such as "sand.mpb". Bump textures modify surface normals. The image used for a bump texture represents the topology or height of the surface relative to the average surface. Dark areas are depressions and light areas are high points. The effect is like embossing the surface with the texture.
#
#
#Procedural texture files
#
#Procedural texture files use mathematical formulas to calculate sample values of the texture. The procedural texture file is compiled, stored, and accessed by the Image program when rendering. for more information see chapter 9, "Procedural Texture Files (.cxc, .cxb. and .cxs)".
#
#Syntax
#
#The following syntax describes the texture map statements that apply to .mtl files. These statements can be used alone or with any combination of options. The options and their arguments are inserted between the keyword and the "filename".
#
#map_Ka -options args filename
#
#Specifies that a color texture file or a color procedural texture file is applied to the ambient reflectivity of the material. During rendering, the "map_Ka" value is multiplied by the "Ka" value.
#
#"filename" is the name of a color texture file (.mpc), a color procedural texture file (.cxc), or an image file.
#
#Tip To make sure that the texture retains its original look, use the .rfl file "ident" as the underlying material. This applies to the "map_Ka", "map_Kd", and "map_Ks" statements. For more information on .rfl files, see chapter 8, "Spectral Curve File (.rfl)".
#
#The options for the "map_Ka" statement are listed below. These options are described in detail in "Options for texture map statements" on page 5-18.
#
#-blendu on | off
#-blendv on | off
#-cc on | off
#-clamp on | off
#-mm base gain
#-o u v w
#-s u v w
#-t u v w
#-texres value
#
#
#map_Kd -options args filename
#
#Specifies that a color texture file or color procedural texture file is linked to the diffuse reflectivity of the material. During rendering, the map_Kd value is multiplied by the Kd value.
#
#"filename" is the name of a color texture file (.mpc), a color procedural texture file (.cxc), or an image file.
#
#The options for the map_Kd statement are listed below. These options are described in detail in "Options for texture map statements" on page 5-18.
#
#-blendu on | off
#-blendv on | off
#-cc on | off
#-clamp on | off
#-mm base gain
#-o u v w
#-s u v w
#-t u v w
#-texres value
#
#
#map_Ks -options args filename
#
#Specifies that a color texture file or color procedural texture file is linked to the specular reflectivity of the material. During rendering, the map_Ks value is multiplied by the Ks value.
#
#"filename" is the name of a color texture file (.mpc), a color procedural texture file (.cxc), or an image file.
#
#The options for the map_Ks statement are listed below. These options are described in detail in "Options for texture map statements" on page 5-18.
#
#-blendu on | off
#-blendv on | off
#-cc on | off
#-clamp on | off
#-mm base gain
#-o u v w
#-s u v w
#-t u v w
#-texres value
#
#
#map_Ns -options args filename
#
#Specifies that a scalar texture file or scalar procedural texture file is linked to the specular exponent of the material. During rendering, the map_Ns value is multiplied by the Ns value.
#
#"filename" is the name of a scalar texture file (.mps), a scalar procedural texture file (.cxs), or an image file.
#
#The options for the map_Ns statement are listed below. These options are described in detail in "Options for texture map statements" on page 5-18.
#
#-blendu on | off
#-blendv on | off
#-clamp on | off
#-imfchan r | g | b | m | l | z
#-mm base gain
#-o u v w
#-s u v w
#-t u v w
#-texres value
#
#
#map_d -options args filename
#
#Specifies that a scalar texture file or scalar procedural texture file is linked to the dissolve of the material. During rendering, the map_d value is multiplied by the d value.
#
#"filename" is the name of a scalar texture file (.mps), a scalar procedural texture file (.cxs), or an image file.
#
#The options for the map_d statement are listed below. These options are described in detail in "Options for texture map statements" on page 5-18.
#
#-blendu on | off
#-blendv on | off
#-clamp on | off
#-imfchan r | g | b | m | l | z
#-mm base gain
#-o u v w
#-s u v w
#-t u v w
#-texres value
