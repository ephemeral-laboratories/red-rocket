package garden.ephemeral.rocket.camera

import garden.ephemeral.rocket.World
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.patterns.UV

/**
 * Different strategies for sampling pixel values in the camera.
 */
interface SamplingStrategy {

    /**
     * Samples the color at the given pixel.
     *
     * @param camera the camera.
     * @param world the world.
     * @param px the pixel's X offset.
     * @param py the pixel's Y offset.
     */
    fun sample(camera: Camera, world: World, px: Int, py: Int): Color

    companion object {
        /**
         * Sampling strategy which takes a single sample at the middle of the pixel.
         * (The original behaviour specified by the book.)
         */
        val center: SamplingStrategy = object : SamplingStrategy {
            override fun sample(camera: Camera, world: World, px: Int, py: Int): Color {
                // Always samples at the middle of the pixel
                val ray = camera.rayForPixelOffset(px + 0.5, py + 0.5)
                return world.colorAt(ray)
            }
        }

        /** Sampling strategy using 2 points per pixel. */
        val multi2 = multi(UV(0.25, 0.25), UV(0.75, 0.75))

        /** Sampling strategy using 4 points per pixel. */
        val multi4 = multi(UV(0.375, 0.125), UV(0.875, 0.375), UV(0.125, 0.625), UV(0.625, 0.875))

        /** Sampling strategy using 8 points per pixel. */
        val multi8 = multi(
            UV(0.9375, 0.0625),
            UV(0.3125, 0.1875),
            UV(0.5625, 0.3125),
            UV(0.0625, 0.4375),
            UV(0.8125, 0.5625),
            UV(0.4375, 0.6875),
            UV(0.1875, 0.8125),
            UV(0.6875, 0.9375)
        )

        /** Sampling strategy using 16 points per pixel. */
        val multi16 = multi(
            UV(0.0625, 0.0), UV(0.5, 0.0625), UV(0.25, 0.125), UV(0.6875, 0.1875),
            UV(0.9375, 0.25), UV(0.4375, 0.3125), UV(0.1875, 0.375), UV(0.75, 0.4375),
            UV(0.0, 0.5), UV(0.5625, 0.5625), UV(0.3125, 0.625), UV(0.8125, 0.6875),
            UV(0.125, 0.75), UV(0.625, 0.8125), UV(0.375, 0.875), UV(0.875, 0.9375)
        )

        private fun multi(vararg sampleOffsets: UV): SamplingStrategy = object : SamplingStrategy {
            private val weightPerSample = 1.0 / sampleOffsets.size

            override fun sample(camera: Camera, world: World, px: Int, py: Int): Color {
                return sampleOffsets
                    .map { offset ->
                        val ray = camera.rayForPixelOffset(px + offset.u, py + offset.v)
                        world.colorAt(ray)
                    }
                    .fold(Color.black) { accumulator, sampledColor ->
                        accumulator + sampledColor * weightPerSample
                    }
            }
        }
    }
}
