package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Matrix

object MuellerMatrices {

    val LinearPolarizerHorizontal = Matrix(4, 4, doubleArrayOf(
        0.5, 0.5, 0.0, 0.0,
        0.5, 0.5, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0,
    ))

    val LinearPolarizerVertical = Matrix(4, 4, doubleArrayOf(
        0.5, -0.5, 0.0, 0.0,
       -0.5,  0.5, 0.0, 0.0,
        0.0,  0.0, 0.0, 0.0,
        0.0,  0.0, 0.0, 0.0,
    ))

    val LinearPolarizerPlus45 = Matrix(4, 4, doubleArrayOf(
        0.5, 0.0, 0.5, 0.0,
        0.0, 0.0, 0.0, 0.0,
        0.5, 0.0, 0.5, 0.0,
        0.0, 0.0, 0.0, 0.0,
    ))

    val LinearPolarizerMinus45 = Matrix(4, 4, doubleArrayOf(
        0.5, 0.0, -0.5, 0.0,
        0.0, 0.0,  0.0, 0.0,
       -0.5, 0.0,  0.5, 0.0,
        0.0, 0.0,  0.0, 0.0,
    ))

    val QuarterWavePlateFastAxisVertical = Matrix(4, 4, doubleArrayOf(
        1.0, 0.0, 0.0,  0.0,
        0.0, 1.0, 0.0,  0.0,
        0.0, 0.0, 0.0, -1.0,
        0.0, 0.0, 1.0,  0.0,
    ))

    val QuarterWavePlateFastAxisHorizontal = Matrix(4, 4, doubleArrayOf(
        1.0, 0.0,  0.0, 0.0,
        0.0, 1.0,  0.0, 0.0,
        0.0, 0.0,  0.0, 1.0,
        0.0, 0.0, -1.0, 0.0,
    ))

    val HalfWavePlate = Matrix(4, 4, doubleArrayOf(
        1.0, 0.0,  0.0,  0.0,
        0.0, 1.0,  0.0,  0.0,
        0.0, 0.0, -1.0,  0.0,
        0.0, 0.0,  0.0, -1.0,
    ))
}