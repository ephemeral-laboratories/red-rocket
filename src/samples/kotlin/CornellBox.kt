package garden.ephemeral.rocket

import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.dsl.WorldBuilder
import garden.ephemeral.rocket.dsl.render
import garden.ephemeral.rocket.shapes.Triangle
import garden.ephemeral.rocket.spectra.SpectralDataFiles

fun main() = render {
    val materials = object {
        // TODO: Keep spectra instead
        val white = Material.build {
            color = SpectralDataFiles.readDoubleSpectralData("/garden/ephemeral/rocket/cornell-box-white.txt").createSpectrum().toLinearRgbReflectance()
        }
        val green = Material.build {
            color = SpectralDataFiles.readDoubleSpectralData("/garden/ephemeral/rocket/cornell-box-green.txt").createSpectrum().toLinearRgbReflectance()
        }
        val red = Material.build {
            color = SpectralDataFiles.readDoubleSpectralData("/garden/ephemeral/rocket/cornell-box-red.txt").createSpectrum().toLinearRgbReflectance()
        }
        val light = Material.build {
            color = SpectralDataFiles.readDoubleSpectralData("/garden/ephemeral/rocket/cornell-box-light-reflectance.txt").createSpectrum().toLinearRgbReflectance()
            emission = SpectralDataFiles.readDoubleSpectralData("/garden/ephemeral/rocket/cornell-box-light-emission.txt").createSpectrum().toLinearRgbEmission()
        }
    }

    World {
        // Floor
        quad(552.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 559.2, 549.6, 0.0, 559.2, materials.white)

        // Light
        quad(343.0, 548.8, 227.0, 343.0, 548.8, 332.0, 213.0, 548.8, 332.0, 213.0, 548.8, 227.0, materials.light)

        // Ceiling
        quad(556.0, 548.8, 0.0, 556.0, 548.8, 559.2, 0.0, 548.8, 559.2, 0.0, 548.8, 0.0, materials.white)

        // Back wall
        quad(549.6, 0.0, 559.2, 0.0, 0.0, 559.2, 0.0, 548.8, 559.2, 556.0, 548.8, 559.2, materials.white)

        // Right wall
        quad(0.0, 0.0, 559.2, 0.0, 0.0, 0.0, 0.0, 548.8, 0.0, 0.0, 548.8, 559.2, materials.green)

        // Left wall
        quad(552.8, 0.0, 0.0, 549.6, 0.0, 559.2, 556.0, 548.8, 559.2, 556.0, 548.8, 0.0, materials.red)

        // Short block
        Group {
            quad(130.0, 165.0, 65.0, 82.0, 165.0, 225.0, 240.0, 165.0, 272.0, 290.0, 165.0, 114.0, materials.white)
            quad(290.0, 0.0, 114.0, 290.0, 165.0, 114.0, 240.0, 165.0, 272.0, 240.0, 0.0, 272.0, materials.white)
            quad(130.0, 0.0, 65.0, 130.0, 165.0, 65.0, 290.0, 165.0, 114.0, 290.0, 0.0, 114.0, materials.white)
            quad(82.0, 0.0, 225.0, 82.0, 165.0, 225.0, 130.0, 165.0, 65.0, 130.0, 0.0, 65.0, materials.white)
            quad(240.0, 0.0, 272.0, 240.0, 165.0, 272.0, 82.0, 165.0, 225.0, 82.0, 0.0, 225.0, materials.white)
        }

        // Tall block
        Group {
            quad(423.0, 330.0, 247.0, 265.0, 330.0, 296.0, 314.0, 330.0, 456.0, 472.0, 330.0, 406.0, materials.white)
            quad(423.0, 0.0, 247.0, 423.0, 330.0, 247.0, 472.0, 330.0, 406.0, 472.0, 0.0, 406.0, materials.white)
            quad(472.0, 0.0, 406.0, 472.0, 330.0, 406.0, 314.0, 330.0, 456.0, 314.0, 0.0, 456.0, materials.white)
            quad(314.0, 0.0, 456.0, 314.0, 330.0, 456.0, 265.0, 330.0, 296.0, 265.0, 0.0, 296.0, materials.white)
            quad(265.0, 0.0, 296.0, 265.0, 330.0, 296.0, 423.0, 330.0, 247.0, 423.0, 0.0, 247.0, materials.white)
        }
    }

    // TODO: We need a more physical camera model to really get it right.
//    Camera(
//        Position	278 273 -800
//        Direction	0 0 1
//        Up direction	0 1 0
//        Focal length	0.035
//        Width, height	0.025 0.025
//    )
}

private fun WorldBuilder.quad(
    x1: Double,
    y1: Double,
    z1: Double,
    x2: Double,
    y2: Double,
    z2: Double,
    x3: Double,
    y3: Double,
    z3: Double,
    x4: Double,
    y4: Double,
    z4: Double,
    material: Material
) {
    val p1 = point(x1, y1, z1)
    val p2 = point(x2, y2, z2)
    val p3 = point(x3, y3, z3)
    val p4 = point(x4, y4, z4)
    Group {
        Triangle(p1, p2, p3).apply { this.material = material }
        Triangle(p2, p3, p4).apply { this.material = material }
    }
}
