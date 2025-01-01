package garden.ephemeral.rocket.util

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.expandDims

/**
 * Missing API. Stacks multiple of the same array on top of each other to create
 * an array one dimension higher.
 *
 * I think in vector APIs, this sort of thing was called a "broadcast" operation.
 */
fun <T: Number> mk.stack(arrays: List<MultiArray<T, D1>>, axis: Int): MultiArray<T, D2> {
    require(arrays.isNotEmpty()) { "at least one array should be provided, and only two or more is of any use" }
    var temp = arrays[0].expandDims(axis = axis)
    for (i in 1..<arrays.size) {
        temp = temp.cat(arrays[i].expandDims(axis = axis), axis = axis)
    }
    return temp
}

/**
 * Convenience API. Like [stack] but stacks multiple of the same array.
 */
fun <T: Number> mk.stackNCopies(array: MultiArray<T, D1>, copies: Int, axis: Int): MultiArray<T, D2> {
    return stack(arrays = (0..<copies).map { array }, axis = axis)
}
