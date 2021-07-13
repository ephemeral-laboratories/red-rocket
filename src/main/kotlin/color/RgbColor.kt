package garden.ephemeral.rocket.color

data class RgbColor(val r: Double, val g: Double, val b: Double): Color() {
    override val isBlack: Boolean get() = r == 0.0 && g == 0.0 && b == 0.0

    override operator fun plus(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(r + their.r, g + their.g, b + their.b)
        }
        throw IncompatibleColorException(this, their)
    }

    override operator fun minus(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(r - their.r, g - their.g, b - their.b)
        }
        throw IncompatibleColorException(this, their)
    }

    override operator fun times(scalar: Double): Color {
        return RgbColor(r * scalar, g * scalar, b * scalar)
    }

    override operator fun times(their: Color): Color {
        if (their is RgbColor) {
            return RgbColor(r * their.r, g * their.g, b * their.b)
        }
        throw IncompatibleColorException(this, their)
    }

    override fun toLinearRgbDoubles(): DoubleArray {
        return doubleArrayOf(r, g, b)
    }

}