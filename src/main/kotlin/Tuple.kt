data class Tuple (val x: Double, val y: Double, val z: Double, val w: Double) {
    val point:      Boolean get() = w == 1.0
    val vector:     Boolean get() = w == 0.0
    val magnitude:  Double  get() = Math.sqrt(x * x + y * y + z * z + w * w)
    val red:        Double  get() = x
    val green:      Double  get() = y
    val blue:       Double  get() = z
    val alpha:      Double  get() = w
    val r:          Double  get() = x
    val g:          Double  get() = y
    val b:          Double  get() = z
    val a:          Double  get() = w

    companion object {
        val zero: Tuple = Tuple(0.0, 0.0, 0.0, 0.0)

        fun point(x: Double, y: Double, z: Double) : Tuple {
            return Tuple(x, y, z, 1.0)
        }

        fun vector(x: Double, y: Double, z: Double) : Tuple {
            return Tuple(x, y, z, 0.0)
        }

        fun color(r: Double, g: Double, b: Double, a: Double = 0.0) : Tuple {
            return Tuple(r, g, b, a)
        }
    }

    operator fun plus(their: Tuple): Tuple {
        return Tuple(x + their.x, y + their.y, z + their.z, w + their.w)
    }

    operator fun minus(their: Tuple): Tuple {
        return Tuple(x - their.x, y - their.y, z - their.z, w - their.w)
    }

    operator fun unaryMinus(): Tuple {
        return Tuple(-x, -y, -z, -w)
    }

    operator fun times(scalar: Double): Tuple {
        return Tuple(x * scalar, y * scalar, z * scalar, w * scalar)
    }

    operator fun times(their: Tuple): Tuple {
        return Tuple(x * their.x, y * their.y, z * their.z, w * their.w)
    }

    operator fun div(scalar: Double): Tuple {
        return Tuple(x / scalar, y / scalar, z / scalar, w / scalar)
    }

    operator fun div(their: Tuple): Tuple {
        return Tuple(x / their.x, y / their.y, z / their.z, w / their.w)
    }

    fun normalize(): Tuple {
        return div(magnitude)
    }

    fun isCloseTo(their: Tuple, epsilon: Double) : Boolean {
        return Math.abs(x - their.x) <= epsilon
                && Math.abs(y - their.y) <= epsilon
                && Math.abs(z - their.z) <= epsilon
                && Math.abs(w - their.w) <= epsilon
    }

    fun dot(their: Tuple): Double {
        return x * their.x + y * their.y + z * their.z;
    }

    fun cross(their: Tuple): Tuple {
        if (!vector || !their.vector) {
            throw IllegalStateException("cross product only makes sense for vectors")
        }
        return vector(
            y * their.z - z * their.y,
            z * their.x - x * their.z,
            x * their.y - y * their.x)
    }

    fun toIntPPM(): String {
        return "${floatToClampedInt(r)} ${floatToClampedInt(g)} ${floatToClampedInt(b)}"
    }

    fun floatToClampedInt(f: Double): Int {
        return (f * 256).toInt().coerceIn(0, 255)
    }
}
