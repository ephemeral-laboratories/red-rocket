package garden.ephemeral.rocket.camera

import garden.ephemeral.rocket.Canvas
import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.Matrix.Companion.identity4x4
import garden.ephemeral.rocket.Ray
import garden.ephemeral.rocket.Tuple.Companion.origin
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.World
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.spectra.DoubleSpectrum
import garden.ephemeral.rocket.spectra.SpectralShape
import garden.ephemeral.rocket.spectra.Wavelength
import garden.ephemeral.rocket.util.Angle
import garden.ephemeral.rocket.util.tan
import garden.ephemeral.rocket.util.toImmutableDoubleArray

data class Camera(
    val hSize: Int,
    val vSize: Int,
    val fieldOfView: Angle,
    val exposure: Double = 1.0,
    val samplingStrategy: SamplingStrategy = SamplingStrategy.center,
) {
    private val halfWidth: Double
    private val halfHeight: Double
    val pixelSize: Double
    var transform: Matrix = identity4x4

    init {
        val halfView = tan(fieldOfView / 2)
        val aspect = hSize / vSize.toDouble()
        if (aspect >= 1) {
            halfWidth = halfView
            halfHeight = halfView / aspect
        } else {
            halfWidth = halfView * aspect
            halfHeight = halfView
        }
        pixelSize = (halfWidth * 2) / hSize
    }

    /**
     * Computes a ray for an offset in pixels. Permits values not at the edge of pixels.
     *
     * @param pixelXOffset the X offset in pixels.
     * @param pixelYOffset the Y offset in pixels.
     */
    fun rayForPixelOffset(pixelXOffset: Double, pixelYOffset: Double): Ray {
        // Offset from the edge of the canvas to the pixel's center
        val xOffset = pixelXOffset * pixelSize
        val yOffset = pixelYOffset * pixelSize

        // Untransformed coordinates of the pixel in world space.
        // (remember that the camera looks toward -z, so +x is to the *left*.)
        val worldX = halfWidth - xOffset
        val worldY = halfHeight - yOffset

        // Using the camera matrix, transform the canvas point and the origin,
        // and then compute the ray's direction vector.
        // (remember that the canvas is at z=-1)
        val pixel = transform.inverse * point(worldX, worldY, -1.0)
        val origin = transform.inverse * origin
        val direction = (pixel - origin).normalize()

        return Ray(origin, direction)
    }

    /**
     * Renders a colour image of the world in the camera.
     *
     * @param world the world.
     * @return the captured canvas.
     */
    fun render(world: World): Canvas {
        return Canvas(hSize, vSize).applyFill { px, py ->
            colorAtPixel(world, px, py)
        }
    }

    fun render2(world: World, wavelength: Wavelength): Canvas {
        return Canvas(hSize, vSize).applyFill { px, py ->
            val intensity = intensityAtPixel(world, px, py, wavelength)
            val value = intensity * exposure

            Color.grey(value)
        }
    }

    fun render(world: World, spectralShape: SpectralShape = SpectralShape.Default): Canvas {
        return Canvas(hSize, vSize).applyFill { px, py ->
            val intensities = spectralShape.wavelengths.map { wavelength ->
                intensityAtPixel(world, px, py, wavelength)
            }.toImmutableDoubleArray()

            val spectrum = DoubleSpectrum(spectralShape, intensities * exposure)

            // TODO: Capture done by the camera has its own curves for RGB which we should simulate as well
            spectrum.toCieXyzEmission().toLinearRgb()
        }
    }

    fun colorAtPixel(world: World, px: Int, py: Int): Color {
        return samplingStrategy.sample(this@Camera, world, px, py)
    }

    fun intensityAtPixel(world: World, px: Int, py: Int, wavelength: Wavelength): Double {
        return samplingStrategy.sample(this@Camera, world, px, py, wavelength)
    }
}
