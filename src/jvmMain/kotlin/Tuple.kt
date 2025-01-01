package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.yzxw
import garden.ephemeral.rocket.util.zxyw
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.jetbrains.kotlinx.multik.ndarray.operations.unaryMinus
import kotlin.math.sqrt

data class Tuple(val cells: D1Array<Double>) {
    constructor(x: Double, y: Double, z: Double, w: Double) : this(doubleArrayOf(x, y, z, w))
    private constructor(cells: DoubleArray) : this(mk.ndarray(cells))

    val isPoint: Boolean
        get() = w == 1.0
    val isVector: Boolean
        get() = w == 0.0
    val x: Double
        get() = cells[0]
    val y: Double
        get() = cells[1]
    val z: Double
        get() = cells[2]
    val w: Double
        get() = cells[3]

    val magnitude: Double
        get() = sqrt(dot(this))

    companion object {
        val zero = vector(0.0, 0.0, 0.0)
        val origin = point(0.0, 0.0, 0.0)

        fun point(x: Double, y: Double, z: Double): Tuple { return Tuple(x, y, z, 1.0) }
        fun vector(x: Double, y: Double, z: Double): Tuple { return Tuple(x, y, z, 0.0) }
    }

    operator fun plus(their: Tuple): Tuple {
        return Tuple(cells + their.cells)
    }

    operator fun minus(their: Tuple): Tuple {
        return Tuple(cells - their.cells)
    }

    operator fun unaryMinus(): Tuple {
        return Tuple(-cells)
    }

    operator fun times(scalar: Double): Tuple {
        return Tuple(cells * scalar)
    }

    operator fun times(their: Tuple): Tuple {
        return Tuple(cells * their.cells)
    }

    operator fun div(scalar: Double): Tuple {
        return Tuple(cells / scalar)
    }

    operator fun div(their: Tuple): Tuple {
        return Tuple(cells / their.cells)
    }

    fun normalize(): Tuple {
        return this / magnitude
    }

    fun dot(their: Tuple): Double {
        return cells dot their.cells
    }

    fun cross(their: Tuple): Tuple {
        if (!isVector || !their.isVector) {
            throw IllegalStateException("cross product only makes sense for vectors")
        }

        return Tuple((cells.yzxw * their.cells.zxyw) - (cells.zxyw * their.cells.yzxw))
    }

    fun reflect(normal: Tuple): Tuple {
        return this - normal * 2.0 * dot(normal)
    }
}
