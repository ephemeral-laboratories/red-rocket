import kotlin.math.sqrt

data class Tuple (val cells: DoubleArray) {
    val point:      Boolean get() = w == 1.0
    val vector:     Boolean get() = w == 0.0
    val magnitude:  Double  get() = sqrt(cells.sumByDouble { cell -> cell * cell })
    val x:          Double  get() = cells[0]
    val y:          Double  get() = cells[1]
    val z:          Double  get() = cells[2]
    val w:          Double  get() = cells[3]
    val r:          Double  get() = cells[0]
    val g:          Double  get() = cells[1]
    val b:          Double  get() = cells[2]
    val a:          Double  get() = cells[3]

    constructor(x: Double, y: Double, z: Double) : this(doubleArrayOf(x, y, z))
    constructor(x: Double, y: Double, z: Double, w: Double) : this(doubleArrayOf(x, y, z, w))

    companion object {
        val zero: Tuple = Tuple(0.0, 0.0, 0.0, 0.0)

        fun point(x: Double, y: Double, z: Double) : Tuple { return Tuple(x, y, z, 1.0) }
        fun vector(x: Double, y: Double, z: Double) : Tuple { return Tuple(x, y, z, 0.0) }
        fun color(r: Double, g: Double, b: Double, a: Double) : Tuple { return Tuple(r, g, b, a) }
        // Feels real dirty to default this to 0.0 but it's passing the tests
        fun color(r: Double, g: Double, b: Double) : Tuple { return Tuple(r, g, b, 0.0) }
        fun color(cells: DoubleArray) : Tuple { return Tuple(cells) }
    }

    private fun unaryOp(op: (Double) -> Double): Tuple {
        val result = DoubleArray(4)
        (0 until 4).forEach { index -> result[index] = op(cells[index]) }
        return Tuple(result)
    }

    private fun binaryOp(their: Tuple, op: (Double, Double) -> Double): Tuple {
        val result = DoubleArray(4)
        (0 until 4).forEach { index -> result[index] = op(cells[index], their.cells[index]) }
        return Tuple(result)
    }

    operator fun plus(their: Tuple): Tuple {
        return binaryOp(their) { a, b -> a + b }
    }

    operator fun minus(their: Tuple): Tuple {
        return binaryOp(their) { a, b -> a - b }
    }

    operator fun unaryMinus(): Tuple {
        return unaryOp { a -> -a }
    }

    operator fun times(scalar: Double): Tuple {
        return unaryOp { a -> a * scalar }
    }

    operator fun times(their: Tuple): Tuple {
        return binaryOp(their) { a, b -> a * b }
    }

    operator fun div(scalar: Double): Tuple {
        return unaryOp { a -> a / scalar }
    }

    operator fun div(their: Tuple): Tuple {
        return binaryOp(their) { a, b -> a / b }
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
        return x * their.x + y * their.y + z * their.z + w * their.w;
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

    fun toInts(): IntArray {
        return intArrayOf(floatToClampedInt(r), floatToClampedInt(g), floatToClampedInt(b), floatToClampedInt(a))
    }

    private fun floatToClampedInt(f: Double): Int {
        return (f * 256).toInt().coerceIn(0, 255)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tuple

        if (!cells.contentEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.contentHashCode()
    }
}