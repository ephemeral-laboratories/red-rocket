package garden.ephemeral.rocket.util

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.get

// TODO: Is there a fast way to implement this swizzle, as was the case for DoubleVector?
//@Suppress("SpellCheckingInspection")
//private val yzxwShuffle = mapOf(0 to RInt(1), 1 to RInt(2), 2 to RInt(0))
//
//@Suppress("SpellCheckingInspection")
//private val zxywShuffle = mapOf(0 to RInt(2), 1 to RInt(0), 2 to RInt(1))
//
//@Suppress("SpellCheckingInspection")
//val <T> D1Array<T>.yzxw: D1Array<T>
//    get() = slice(yzxwShuffle)
//
//@Suppress("SpellCheckingInspection")
//val <T> D1Array<T>.zxyw: D1Array<T>
//    get() = slice(zxywShuffle)

@Suppress("SpellCheckingInspection")
inline val <reified T : Number> D1Array<T>.yzxw: D1Array<T>
    get() = mk.ndarray(mk[this[1], this[2], this[0], this[3]])

@Suppress("SpellCheckingInspection")
inline val <reified T : Number> D1Array<T>.zxyw: D1Array<T>
    get() = mk.ndarray(mk[this[2], this[0], this[1], this[3]])
