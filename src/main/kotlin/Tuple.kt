package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.minus
import garden.ephemeral.rocket.util.plus
import garden.ephemeral.rocket.util.times
import garden.ephemeral.rocket.util.unaryMinus
import garden.ephemeral.rocket.util.yzxw
import garden.ephemeral.rocket.util.zxyw
import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import kotlin.math.sqrt

data class Tuple(val cells: DoubleVector) {
    constructor(x: Double, y: Double, z: Double, w: Double) : this(doubleArrayOf(x, y, z, w))
    private constructor(cells: DoubleArray) : this(DoubleVector.fromArray(DoubleVector.SPECIES_256, cells, 0))

    val isPoint: Boolean
        get() = w == 1.0
    val isVector: Boolean
        get() = w == 0.0
    val x: Double
        get() = cells.lane(0)
    val y: Double
        get() = cells.lane(1)
    val z: Double
        get() = cells.lane(2)
    val w: Double
        get() = cells.lane(3)

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
        return (cells * their.cells).reduceLanes(VectorOperators.ADD)
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
