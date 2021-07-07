Feature: Material Library (MTL) Illumination Models

  # TODO: Implementations of illumination models (probably warrants its own feature file)

  # The following list defines the terms and vectors that are used in the
  # illumination model equations:
  #
  # Term  Definition
  #
  # Ft    Fresnel reflectance
  # Ft    Fresnel transmittance
  # Ia    [intensity of] ambient light
  # I     light intensity
  # Ir    intensity from reflected direction (reflection map and/or ray tracing)
  # It    intensity from transmitted direction
  # Ka    ambient reflectance
  # Kd    diffuse reflectance
  # Ks    specular reflectance
  # Tf    transmission filter
  #
  # Vector  Definition
  #
  # H       unit vector bisector between L and V
  # L       unit light vector
  # N       unit surface normal
  # V       unit view vector

  Scenario: Illumination model 0
    # 0  This is a constant color illumination model.  The color is the
    # specified Kd for the material.  The formula is:
    #
    # color = Kd

  Scenario: Illumination model 1
    # 1  This is a diffuse illumination model using Lambertian shading. The
    # color includes an ambient constant term and a diffuse shading term for
    # each light source.  The formula is
    #
    # color = KaIa + Kd { SUM j=1..ls, (N * Lj)Ij }

  Scenario: Illumination model 2
    # 2  This is a diffuse and specular illumination model using Lambertian
    # shading and Blinn's interpretation of Phong's specular illumination
    # model (BLIN77).  The color includes an ambient constant term, and a
    # diffuse and specular shading term for each light source.  The formula
    # is:
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks { SUM j=1..ls, ((H*Hj)^Ns)Ij }

  Scenario: Illumination model 3
    # 3  This is a diffuse and specular illumination model with reflection
    # using Lambertian shading, Blinn's interpretation of Phong's specular
    # illumination model (BLIN77), and a reflection term similar to that in
    # Whitted's illumination model (WHIT80).  The color includes an ambient
    # constant term and a diffuse and specular shading term for each light
    # source.  The formula is:
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks ({ SUM j=1..ls, ((H*Hj)^Ns)Ij } + Ir)
    #
    # Ir = (intensity of reflection map) + (ray trace)

  Scenario: Illumination model 4
    # 4  The diffuse and specular illumination model used to simulate glass
    # is the same as illumination model 3.  When using a very low dissolve
    # (approximately 0.1), specular highlights from lights or reflections
    # become imperceptible.
    #
    # Simulating glass requires an almost transparent object that still
    # reflects strong highlights.  The maximum of the average intensity of
    # highlights and reflected lights is used to adjust the dissolve factor.
    # The formula is:
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks ({ SUM j=1..ls, ((H*Hj)^Ns)Ij } + Ir)

  Scenario: Illumination model 5
    # 5  This is a diffuse and specular shading models similar to
    # illumination model 3, except that reflection due to Fresnel effects is
    # introduced into the equation.  Fresnel reflection results from light
    # striking a diffuse surface at a grazing or glancing angle.  When light
    # reflects at a grazing angle, the Ks value approaches 1.0 for all color
    # samples.  The formula is:
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks ({ SUM j=1..ls, ((H*Hj)^Ns)Ij Fr(Lj*Hj,Ks,Ns)Ij} +
    # Fr(N*V,Ks,Ns)Ir})

  Scenario: Illumination model 6
    # 6  This is a diffuse and specular illumination model similar to that
    # used by Whitted (WHIT80) that allows rays to refract through a surface.
    # The amount of refraction is based on optical density (Ni).  The
    # intensity of light that refracts is equal to 1.0 minus the value of Ks,
    # and the resulting light is filtered by Tf (transmission filter) as it
    # passes through the object.  The formula is:
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks ({ SUM j=1..ls, ((H*Hj)^Ns)Ij } + Ir)
    # + (1.0 - Ks) TfIt

  Scenario: Illumination model 7
    # 7  This illumination model is similar to illumination model 6, except
    # that reflection and transmission due to Fresnel effects has been
    # introduced to the equation.  At grazing angles, more light is reflected
    # and less light is refracted through the object.  The formula is:
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks ({ SUM j=1..ls, ((H*Hj)^Ns)Ij Fr(Lj*Hj,Ks,Ns)Ij} +
    # Fr(N*V,Ks,Ns)Ir})
    #
    # + (1.0 - Kx)Ft (N*V,(1.0-Ks),Ns)TfIt

  Scenario: Illumination model 8
    # 8  This illumination model is similar to illumination model 3 without
    # ray tracing.  The formula is:
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks ({ SUM j=1..ls, ((H*Hj)^Ns)Ij } + Ir)
    #
    # Ir = (intensity of reflection map)

  Scenario: Illumination model 9
    # 9  This illumination model is similar to illumination model 4without
    # ray tracing.  The formula is:
    #
    #
    # color = KaIa
    # + Kd { SUM j=1..ls, (N*Lj)Ij }
    # + Ks ({ SUM j=1..ls, ((H*Hj)^Ns)Ij } + Ir)
    #
    # Ir = (intensity of reflection map)

  Scenario: Illumination model 10
    # 10  This illumination model is used to cast shadows onto an invisible
    # surface.  This is most useful when compositing computer-generated
    # imagery onto live action, since it allows shadows from rendered objects
    # to be composited directly on top of video-grabbed images.  The equation
    # for computation of a shadowmatte is formulated as follows.
    #
    # color = Pixel color.  The pixel color of a shadowmatte material is
    # always black.
    #
    # color = black
    #
    # M = Matte channel value.  This is the image channel which typically
    # represents the opacity of the point on the surface.  To store the shadow
    # in the matte channel of the image, it is calculated as:
    #
    # M = 1 - W / P
    #
    # where:
    #
    # P = Unweighted sum.  This is the sum of all S values for each light:
    #
    # P = S1 + S2 + S3 + .....
    #
    # W = Weighted sum.  This is the sum of all S values, each weighted by
    # the visibility factor (Q) for the light:
    #
    # W = (S1 * Q1) + (S2 * Q2) + .....
    #
    # Q = Visibility factor.  This is the amount of light from a particular
    # light source that reaches the point to be shaded, after traveling
    # through all shadow objects between the light and the point on the
    # surface.  Q = 0 means no light reached the point to be shaded; it was
    # blocked by shadow objects, thus casting a shadow.  Q = 1 means that
    # nothing blocked the light, and no shadow was cast.  0 < Q < 1 means that
    # the light was partially blocked by objects that were partially
    # dissolved.
    #
    # S = Summed brightness.  This is the sum of the spectral sample
    # intensities for a particular light.  The samples are variable, but the
    # default is 3:
    #
    # S = samp1 + samp2 + samp3.
