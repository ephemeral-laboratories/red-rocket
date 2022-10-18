package garden.ephemeral.rocket

import garden.ephemeral.rocket.Transforms.Companion.rotationX
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

fun main() = render {
    World {
        Plane {
            name = "Floor"
            material = Material.build {
                pattern(CheckersPattern(white, black).apply {
                    transform = scaling(0.25, 0.25, 0.25)
                })
                specular(black)
                reflective(grey(0.1))
            }
        }

        Plane {
            name = "Left Wall"
            transform = translation(0.0, 0.0, 5.0) * rotationX(90.deg)
            material = Material.build {
                specular(black)
            }
        }

        Plane {
            name = "Right Wall"
            transform = translation(5.0, 0.0, 0.0) * rotationZ(90.deg)
            material = Material.build {
                specular(black)
            }
        }

        ObjFile("src/files/teapot.obj") {
            name = "Teapot"
            transform = scaling(0.125, 0.125, 0.125) * rotationX((-90).deg)
        }

        PointLight {
            position(point(-12.0, 10.0, -10.0))
            color(linearRgb(1.0, 0.0, 1.0))
            radiantIntensity(10.0)
        }
        PointLight {
            position(point(-8.0, 10.0, -10.0))
            color(linearRgb(0.0, 1.0, 1.0))
            radiantIntensity(10.0)
        }

        Camera(1280, 720, 60.deg, 0.05) {
            transform = viewTransform(point(0.0, 1.0, -5.0), point(0.0, 0.75, 0.0), vector(0.0, 1.0, 0.0))
        }
    }
}
