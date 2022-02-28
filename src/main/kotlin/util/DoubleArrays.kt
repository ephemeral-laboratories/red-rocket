package garden.ephemeral.rocket.util

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorSpecies

object DoubleArrays {
    fun reciprocal(array: DoubleArray): DoubleArray {
        val result = DoubleArray(array.size)
        val species = chooseSpecies(array)
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            val one = DoubleVector.broadcast(species, 1.0)
            while (i + laneCount - 1 < array.size) {
                val vector = DoubleVector.fromArray(species, array, i)
                one.lanewise(VectorOperators.DIV, vector).intoArray(result, i)
                i += laneCount
            }
        }

        while (i < array.size) {
            result[i] = 1.0 / array[i]
            i++
        }

        return result
    }

    fun multiply(array: DoubleArray, scalar: Double): DoubleArray {
        val result = DoubleArray(array.size)
        val species = chooseSpecies(array)
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array.size) {
                val vector = DoubleVector.fromArray(species, array, i)
                vector.mul(scalar).intoArray(result, i)
                i += laneCount
            }
        }

        while (i < array.size) {
            result[i] = array[i] * scalar
            i++
        }

        return result
    }

    fun dotProduct(array1: DoubleArray, array2: DoubleArray): Double {
        require(array1.size == array2.size) { "Required arrays with same size! Got: $array1, $array2"}

        var result = 0.0
        val species = chooseSpecies(array1)
        var i = 0

        if (species != null) {
            val laneCount = species.length()

            while (i + laneCount - 1 < array1.size) {
                val vector1 = DoubleVector.fromArray(species, array1, i)
                val vector2 = DoubleVector.fromArray(species, array2, i)
                result += vector1.mul(vector2).reduceLanes(VectorOperators.ADD)
                i += laneCount
            }
        }

        while (i < array1.size) {
            result += array1[i] * array2[i]
            i++
        }

        return result
    }

    private fun chooseSpecies(array: DoubleArray): VectorSpecies<Double>? {
        val bestForArraySize = when(array.size) {
            1 -> return null
            in (2..3) -> DoubleVector.SPECIES_128
            in (4..7) -> DoubleVector.SPECIES_256
            else -> DoubleVector.SPECIES_512
        }

        val preferred = DoubleVector.SPECIES_PREFERRED
        return if (preferred.length() < bestForArraySize.length()) {
            preferred
        } else {
            bestForArraySize
        }
    }
}