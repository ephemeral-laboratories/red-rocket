package garden.ephemeral.rocket

import kotlin.math.abs
import kotlin.math.sqrt

data class Tuple(val cells: DoubleArray) {
    val point:      Boolean get() = w == 1.0
    val vector:     Boolean get() = w == 0.0
    val magnitude:  Double  get() = sqrt(cells.sumByDouble { cell -> cell * cell })
    val x:          Double  get() = cells[0]
    val y:          Double  get() = cells[1]
    val z:          Double  get() = cells[2]
    val w:          Double  get() = cells[3]

    constructor(x: Double, y: Double, z: Double) : this(doubleArrayOf(x, y, z))
    constructor(x: Double, y: Double, z: Double, w: Double) : this(doubleArrayOf(x, y, z, w))

    companion object {
        val zero = vector(0.0, 0.0, 0.0)
        val origin = point(0.0, 0.0, 0.0)

        fun point(x: Double, y: Double, z: Double) : Tuple { return Tuple(x, y, z, 1.0) }
        fun vector(x: Double, y: Double, z: Double) : Tuple { return Tuple(x, y, z, 0.0) }
    }

    private inline fun unaryOp(op: (Double) -> Double): Tuple {
        val result = DoubleArray(4) { index -> op(cells[index]) }
        return Tuple(result)
    }

    private inline fun binaryOp(their: Tuple, op: (Double, Double) -> Double): Tuple {
        val result = DoubleArray(4) { index -> op(cells[index], their.cells[index]) }
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

    fun isCloseTo(their: Tuple, delta: Double) : Boolean {
        return abs(x - their.x) <= delta
                && abs(y - their.y) <= delta
                && abs(z - their.z) <= delta
                && abs(w - their.w) <= delta
    }

    fun dot(their: Tuple): Double {
        return x * their.x + y * their.y + z * their.z + w * their.w
    }

    fun cross(their: Tuple): Tuple {
        if (!vector || !their.vector) {
            throw IllegalStateException("cross product only makes sense for vectors")
        }
        return vector(
            y * their.z - z * their.y,
            z * their.x - x * their.z,
            x * their.y - y * their.x
        )
    }

    fun reflect(normal: Tuple): Tuple {
        return this - normal * 2.0 * dot(normal)
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
