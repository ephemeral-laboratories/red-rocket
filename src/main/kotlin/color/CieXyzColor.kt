package garden.ephemeral.rocket.color

data class CieXyzColor(val x: Double, val y: Double, val z: Double) : Color() {
    override val isBlack: Boolean get() = x == 0.0 && y == 0.0 && z == 0.0

    override operator fun plus(their: Color): Color {
        if (their is CieXyzColor) {
            return CieXyzColor(x + their.x, y + their.y, z + their.z)
        }
        return this + their.toCieXyz()
    }

    override operator fun minus(their: Color): Color {
        if (their is CieXyzColor) {
            return CieXyzColor(x - their.x, y - their.y, z - their.z)
        }
        return this - their.toCieXyz()
    }

    override operator fun times(scalar: Double): Color {
        return CieXyzColor(x * scalar, y * scalar, z * scalar)
    }

    override operator fun times(their: Color): Color {
        if (their is CieXyzColor) {
            return CieXyzColor(x * their.x, y * their.y, z * their.z)
        }
        return this * their.toCieXyz()
    }

    override fun toCieXyz(): CieXyzColor {
        return this
    }

    override fun toLinearRgbDoubles(): DoubleArray {
        return ColorTransforms.cieXyzToLinearRgb(doubleArrayOf(x, y, z))
    }
}
