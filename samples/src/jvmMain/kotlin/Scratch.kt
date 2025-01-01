package garden.ephemeral.rocket

import garden.ephemeral.rocket.Transforms.Companion.rotationX
import garden.ephemeral.rocket.Transforms.Companion.rotationY
import garden.ephemeral.rocket.Transforms.Companion.rotationZ
import garden.ephemeral.rocket.Transforms.Companion.scaling
import garden.ephemeral.rocket.Transforms.Companion.translation
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import garden.ephemeral.rocket.color.Color.Companion.white
import garden.ephemeral.rocket.dsl.render
import garden.ephemeral.rocket.patterns.CheckersPattern
import garden.ephemeral.rocket.util.deg
import garden.ephemeral.rocket.util.times

fun main() = render {
    World {
        Plane {
            name = "Floor"
            material = Material.build {
                pattern = CheckersPattern(white, black).apply {
                    transform = scaling(0.25, 0.25, 0.25)
                }
                specular = black
                reflective = grey(0.1)
            }
        }

        Plane {
            name = "Left Wall"
            transform = translation(0.0, 0.0, 5.0) * rotationX(90.deg)
            material = Material.build {
                specular = black
            }
        }

        Plane {
            name = "Right Wall"
            transform = translation(5.0, 0.0, 0.0) * rotationZ(90.deg)
            material = Material.build {
                specular = black
            }
        }

        Group {
            transform = translation(0.0, 1.0, 0.5) * rotationX((-45).deg)
            (0..5).forEach { n ->
                Group {
                    name = "Side ${n + 1}"
                    transform = rotationY(n * 60.deg)

                    Sphere {
                        transform = translation(0.0, 0.0, -1.0) *
                            scaling(0.25, 0.25, 0.25)
                    }

                    Cylinder {
                        minimum = 0.0
                        maximum = 1.0
                        transform = translation(0.0, 0.0, -1.0) *
                            rotationY((-30).deg) *
                            rotationZ((-90).deg) *
                            scaling(0.25, 1.0, 0.25)
                    }
                }
            }
        }

        Sphere {
            transform = translation(1.0, 0.5, -1.0) * scaling(0.5, 0.5, 0.5)
            material = Material.build {
                ambient = black
                diffuse = black
                reflective = grey(0.1)
                transparency = grey(1.0)
                refractiveIndex = 1.5
            }
        }

        Sphere {
            transform = translation(-1.0, 0.5, -1.0) * scaling(0.5, 0.5, 0.5)
            material = Material.build {
                reflective = grey(1.0)
            }
        }

        PointLight(point(-12.0, 10.0, -10.0), linearRgb(1.0, 0.0, 1.0))
        PointLight(point(-8.0, 10.0, -10.0), linearRgb(0.0, 1.0, 1.0))
    }

    Camera(500, 250, 60.deg) {
        transform = viewTransform(point(0.0, 1.0, -5.0), point(0.0, 0.5, 0.0), vector(0.0, 1.0, 0.0))
    }
}
