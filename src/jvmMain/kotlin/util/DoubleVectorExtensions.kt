package garden.ephemeral.rocket.util

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorShuffle

@Suppress("SpellCheckingInspection")
private val yzxwShuffle = VectorShuffle.fromValues(DoubleVector.SPECIES_256, 1, 2, 0, 3)

@Suppress("SpellCheckingInspection")
private val zxywShuffle = VectorShuffle.fromValues(DoubleVector.SPECIES_256, 2, 0, 1, 3)

@Suppress("SpellCheckingInspection")
val DoubleVector.yzxw: DoubleVector
    get() = rearrange(yzxwShuffle)

@Suppress("SpellCheckingInspection")
val DoubleVector.zxyw: DoubleVector
    get() = rearrange(zxywShuffle)

operator fun DoubleVector.plus(other: DoubleVector): DoubleVector = add(other)
operator fun DoubleVector.minus(other: DoubleVector): DoubleVector = sub(other)
operator fun DoubleVector.times(other: DoubleVector): DoubleVector = mul(other)
operator fun DoubleVector.times(scalar: Double): DoubleVector = mul(scalar)
operator fun DoubleVector.unaryMinus(): DoubleVector = neg()
