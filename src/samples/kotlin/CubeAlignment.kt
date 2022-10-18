package garden.ephemeral.rocket

import garden.ephemeral.rocket.Transforms.Companion.rotationX
import garden.ephemeral.rocket.Transforms.Companion.rotationY
import garden.ephemeral.rocket.Transforms.Companion.translation
import garden.ephemeral.rocket.Transforms.Companion.viewTransform
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.color.Color.Companion.black
import garden.ephemeral.rocket.color.Color.Companion.grey
import garden.ephemeral.rocket.color.Color.Companion.linearRgb
import garden.ephemeral.rocket.color.Color.Companion.white
import garden.ephemeral.rocket.dsl.WorldBuilder
import garden.ephemeral.rocket.dsl.render
import garden.ephemeral.rocket.patterns.CubeMap
import garden.ephemeral.rocket.patterns.UVAlignCheck
import garden.ephemeral.rocket.shapes.Cube
import garden.ephemeral.rocket.util.deg

fun WorldBuilder.mappedCube(action: Cube.() -> Unit) {
    Cube {
        material = Material.build {
            val blue = linearRgb(0.0, 0.0, 1.0)
            val green = linearRgb(0.0, 1.0, 0.0)
            val cyan = linearRgb(0.0, 1.0, 1.0)
            val red = linearRgb(1.0, 0.0, 0.0)
            val magenta = linearRgb(1.0, 0.0, 1.0)
            val orange = linearRgb(1.0, 0.5, 0.0)
            val yellow = linearRgb(1.0, 1.0, 0.0)
            pattern(
                CubeMap(
                    UVAlignCheck(yellow, cyan, red, blue, orange),
                    UVAlignCheck(cyan, red, yellow, orange, green),
                    UVAlignCheck(red, yellow, magenta, green, white),
                    UVAlignCheck(green, magenta, cyan, white, blue),
                    UVAlignCheck(orange, cyan, magenta, red, yellow),
                    UVAlignCheck(magenta, orange, green, blue, white)
                )
            )
            ambient(grey(0.2))
            specular(black)
            diffuse(grey(0.8))
        }

        action()
    }
}

fun main() = render {
    World {
        PointLight {
            position(point(0.0, 100.0, -100.0))
            color(grey(0.25))
            luminousFlux(250.0)
        }
        PointLight {
            position(point(0.0, -100.0, -100.0))
            color(grey(0.25))
            luminousFlux(250.0)
        }
        PointLight {
            position(point(-100.0, 0.0, -100.0))
            color(grey(0.25))
            luminousFlux(250.0)
        }
        PointLight {
            position(point(100.0, 0.0, -100.0))
            color(grey(0.25))
            luminousFlux(250.0)
        }

        mappedCube {
            transform = translation(-6.0, 2.0, 0.0) * rotationX(45.deg) * rotationY(45.deg)
        }
        mappedCube {
            transform = translation(-2.0, 2.0, 0.0) * rotationX(45.deg) * rotationY(135.deg)
        }
        mappedCube {
            transform = translation(2.0, 2.0, 0.0) * rotationX(45.deg) * rotationY(225.deg)
        }
        mappedCube {
            transform = translation(6.0, 2.0, 0.0) * rotationX(45.deg) * rotationY(315.deg)
        }
        mappedCube {
            transform = translation(-6.0, -2.0, 0.0) * rotationX((-45).deg) * rotationY(45.deg)
        }
        mappedCube {
            transform = translation(-2.0, -2.0, 0.0) * rotationX((-45).deg) * rotationY(135.deg)
        }
        mappedCube {
            transform = translation(2.0, -2.0, 0.0) * rotationX((-45).deg) * rotationY(225.deg)
        }
        mappedCube {
            transform = translation(6.0, -2.0, 0.0) * rotationX((-45).deg) * rotationY(315.deg)
        }
    }

    Camera(800, 400, 45.deg, 0.05) {
        transform = viewTransform(point(0.0, 0.0, -20.0), point(0.0, 0.0, 0.0), vector(0.0, 1.0, 0.0))
    }
}
