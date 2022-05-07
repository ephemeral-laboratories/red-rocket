package garden.ephemeral.rocket.util

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorSpecies

/**
 * Wrapper for a double array to ensure nobody can mutate it.
 */
class ImmutableDoubleArray(private val array: DoubleArray) {
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

    operator fun plus(other: ImmutableDoubleArray): ImmutableDoubleArray {
        return biFunctionOp(other, DoubleVector::add, Double::plus)
    }

    operator fun times(other: ImmutableDoubleArray): ImmutableDoubleArray {
        return biFunctionOp(other, DoubleVector::mul, Double::times)
    }

    operator fun times(scalar: Double): ImmutableDoubleArray {
        return functionOp(
            { v -> v.mul(scalar) },
            { e -> e * scalar }
        )
    }

    operator fun div(other: ImmutableDoubleArray): ImmutableDoubleArray {
        return biFunctionOp(other, DoubleVector::div, Double::div)
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

    private fun requireSameSize(other: ImmutableDoubleArray) {
        require(other.size == size) { "Required arrays with same size! Got: $other, $this"}
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ImmutableDoubleArray) return false
        return array.contentEquals(other.array)
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }
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
