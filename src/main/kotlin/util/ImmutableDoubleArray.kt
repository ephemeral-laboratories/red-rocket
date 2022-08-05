package garden.ephemeral.rocket.util

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorSpecies
import java.lang.Math.fma
import kotlin.math.sqrt

/**
 * Wrapper for a double array to ensure nobody can mutate it.
 */
class ImmutableDoubleArray(private val array: DoubleArray) : Iterable<Double> {
    val size: Int
        get() = array.size
    val lastIndex: Int
        get() = array.lastIndex
    val indices: IntRange
        get() = array.indices

    private val bestVectorSpecies: VectorSpecies<Double>? by lazy {
        val bestForArraySize = when(array.size) {
            1 -> return@lazy null
            in (2..3) -> DoubleVector.SPECIES_128
            in (4..7) -> DoubleVector.SPECIES_256
            else -> DoubleVector.SPECIES_512
        }

        val preferred = DoubleVector.SPECIES_PREFERRED
        if (preferred.length() < bestForArraySize.length()) {
            preferred
        } else {
            bestForArraySize
        }
    }

    operator fun component1(): Double = this[0]
    operator fun component2(): Double = this[1]
    operator fun component3(): Double = this[2]
    operator fun component4(): Double = this[3]

    operator fun get(index: Int): Double = array[index]

    fun copyInto(destination: DoubleArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size) {
        array.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    /**
     * Copies the contents as a mutable `DoubleArray`.
     *
     * @return the copy.
     */
    fun toMutableArray(): DoubleArray {
        return array.copyInto(DoubleArray(size))
    }

    operator fun plus(other: ImmutableDoubleArray) = biFunctionOp(other, DoubleVector::add, Double::plus)

    operator fun plus(scalar: Double) = functionOp(
        { v -> v.add(scalar) },
        { e -> e + scalar }
    )

    operator fun minus(other: ImmutableDoubleArray) = biFunctionOp(other, DoubleVector::sub, Double::minus)

    operator fun minus(scalar: Double) = functionOp(
        { v -> v.sub(scalar) },
        { e -> e - scalar }
    )

    operator fun times(other: ImmutableDoubleArray) = biFunctionOp(other, DoubleVector::mul, Double::times)

    operator fun times(scalar: Double) = functionOp(
        { v -> v.mul(scalar) },
        { e -> e * scalar }
    )

    operator fun div(other: ImmutableDoubleArray) = biFunctionOp(other, DoubleVector::div, Double::div)

    fun reciprocal(): ImmutableDoubleArray {
        val result = DoubleArray(this.array.size)
        val species = this.bestVectorSpecies
        var i = 0
        if (species != null) {
            val laneCount = species.length()
            // XXX: Is there really no simpler operation that can avoid this broadcast? :(
            val one = DoubleVector.broadcast(species, 1.0)

            while (i + laneCount - 1 < this.array.size) {
                val vector1 = DoubleVector.fromArray(species, this.array, i)
                one.div(vector1).intoArray(result, i)
                i += laneCount
            }
        }

        while (i < this.array.size) {
            result[i] = 1.0 / this.array[i]
            i++
        }

        return ImmutableDoubleArray(result)
    }

    fun sqrt() = functionOp(
        { v -> v.sqrt() },
        { e -> sqrt(e) }
    )

    /**
     * Fused multiply and add.
     *
     * Computes `a * b + c` using the native FMA instruction when possible.
     *
     * @param b the array to multiply by.
     * @param c the array to add afterwards.
     * @return the resulting array.
     */
    fun fma(b: ImmutableDoubleArray, c: ImmutableDoubleArray): ImmutableDoubleArray {
        requireSameSize(b)
        requireSameSize(c)

        val result = DoubleArray(array.size)
        val species = bestVectorSpecies
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array.size) {
                val vector1 = DoubleVector.fromArray(species, array, i)
                val vector2 = DoubleVector.fromArray(species, b.array, i)
                val vector3 = DoubleVector.fromArray(species, c.array, i)
                vector1.fma(vector2, vector3).intoArray(result, i)
                i += laneCount
            }
        }

        while (i < array.size) {
            result[i] = fma(array[i], b.array[i], c.array[i])
            i++
        }

        return ImmutableDoubleArray(result)
    }

    /**
     * Fused multiply and add.
     *
     * Computes `a * b + c` using the native FMA instruction when possible.
     *
     * @param b the array to multiply by.
     * @param c the scalar to add afterwards.
     * @return the resulting array.
     */
    fun fma(b: ImmutableDoubleArray, c: Double): ImmutableDoubleArray {
        requireSameSize(b)

        val result = DoubleArray(array.size)
        val species = bestVectorSpecies
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array.size) {
                val vector1 = DoubleVector.fromArray(species, array, i)
                val vector2 = DoubleVector.fromArray(species, b.array, i)
                vector1.lanewise(VectorOperators.FMA, vector2, c).intoArray(result, i)
                i += laneCount
            }
        }

        while (i < array.size) {
            result[i] = fma(array[i], b.array[i], c)
            i++
        }

        return ImmutableDoubleArray(result)
    }

    /**
     * Fused multiply and add.
     *
     * Computes `a * b + c` using the native FMA instruction when possible.
     *
     * @param b the scalar to multiply by.
     * @param c the scalar to add afterwards.
     * @return the resulting array.
     */
    fun fma(b: Double, c: Double) = functionOp(
        { v -> v.fma(b, c) },
        { e -> fma(e, b, c) }
    )

    fun rsqrt(): ImmutableDoubleArray {
        // XXX: No direct way to get rsqrt? Math also lacks rsqrt() function :(
        return sqrt().reciprocal()
    }

    private inline fun functionOp(
        vectorOp: (DoubleVector) -> DoubleVector,
        elementOp: (Double) -> Double
    ): ImmutableDoubleArray {
        val result = DoubleArray(array.size)
        val species = bestVectorSpecies
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array.size) {
                val vector = DoubleVector.fromArray(species, array, i)
                vectorOp(vector).intoArray(result, i)
                i += laneCount
            }
        }

        while (i < array.size) {
            result[i] = elementOp(array[i])
            i++
        }

        return ImmutableDoubleArray(result)
    }

    private inline fun biFunctionOp(
        other: ImmutableDoubleArray,
        vectorOp: (DoubleVector, DoubleVector) -> DoubleVector,
        elementOp: (Double, Double) -> Double
    ): ImmutableDoubleArray {
        requireSameSize(other)

        val result = DoubleArray(array.size)
        val species = bestVectorSpecies
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array.size) {
                val vector1 = DoubleVector.fromArray(species, array, i)
                val vector2 = DoubleVector.fromArray(species, other.array, i)
                vectorOp(vector1, vector2).intoArray(result, i)
                i += laneCount
            }
        }

        while (i < array.size) {
            result[i] = elementOp(array[i], other.array[i])
            i++
        }

        return ImmutableDoubleArray(result)
    }

    fun dotProduct(other: ImmutableDoubleArray): Double {
        requireSameSize(other)

        var result = 0.0
        val species = bestVectorSpecies
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array.size) {
                val vector1 = DoubleVector.fromArray(species, array, i)
                val vector2 = DoubleVector.fromArray(species, other.array, i)
                result += vector1.mul(vector2).reduceLanes(VectorOperators.ADD)
                i += laneCount
            }
        }

        while (i < array.size) {
            result += array[i] * other.array[i]
            i++
        }

        return result
    }

    fun sum(): Double {
        var result = 0.0
        val species = bestVectorSpecies
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array.size) {
                val vector = DoubleVector.fromArray(species, array, i)
                result += vector.reduceLanes(VectorOperators.ADD)
                i += laneCount
            }
        }

        while (i < array.size) {
            result += array[i]
            i++
        }

        return result
    }

    fun slice(range: IntRange): ImmutableDoubleArray {
        return ImmutableDoubleArray(array.sliceArray(range))
    }

    fun slice(indices: Collection<Int>): ImmutableDoubleArray {
        return ImmutableDoubleArray(array.sliceArray(indices))
    }

    fun sliceAsDoubleVector(offset: Int): DoubleVector {
        return DoubleVector.fromArray(DoubleVector.SPECIES_256, array, offset)
    }

    fun forEachIndexed(action: (Int, Double) -> Unit) = array.forEachIndexed(action)

    fun forEach(action: (Double) -> Unit) = array.forEach(action)

    fun count(predicate: (Double) -> Boolean) = array.count(predicate)

    override fun iterator(): DoubleIterator = array.iterator()

    private fun requireSameSize(other: ImmutableDoubleArray) {
        require(other.size == size) { "Required arrays with same size! Got: $other, $this"}
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ImmutableDoubleArray) return false
        return array.contentEquals(other.array)
    }

    override fun hashCode(): Int = array.contentHashCode()

    override fun toString(): String = array.contentToString()
}

/**
 * Creates a double array.
 */
fun immutableDoubleArrayOf(vararg values: Double): ImmutableDoubleArray {
    return ImmutableDoubleArray(doubleArrayOf(*values))
}

/**
 * Builds an immutable double array.
 *
 * @param action a callback which is passed the list to build into.
 * @return the array.
 */
fun buildImmutableDoubleArray(action: MutableList<Double>.() -> Unit): ImmutableDoubleArray {
    return buildList(action).toImmutableDoubleArray()
}

/**
 * Builds an immutable double array.
 *
 * @param capacity the capacity to give the list initially.
 * @param action a callback which is passed the list to build into.
 * @return the array.
 */
fun buildImmutableDoubleArray(capacity: Int, action: MutableList<Double>.() -> Unit): ImmutableDoubleArray {
    return buildList(capacity, action).toImmutableDoubleArray()
}

/**
 * Converts a collection of double values to an immutable array.
 *
 * @receiver the collection.
 * @return the array.
 */
fun Collection<Double>.toImmutableDoubleArray(): ImmutableDoubleArray {
    return ImmutableDoubleArray(toDoubleArray())
}
