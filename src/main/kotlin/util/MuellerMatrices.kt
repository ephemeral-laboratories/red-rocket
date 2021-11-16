package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Matrix
import kotlin.math.cos
import kotlin.math.sin

object MuellerMatrices {
    val LinearPolarizerHorizontal = Matrix(
        4, 4,
        doubleArrayOf(
            0.5, 0.5, 0.0, 0.0,
            0.5, 0.5, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
        )
    )

    val LinearPolarizerVertical = Matrix(
        4, 4,
        doubleArrayOf(
            0.5, -0.5, 0.0, 0.0,
            -0.5, 0.5, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
        )
    )

    val LinearPolarizerPlus45 = Matrix(
        4, 4,
        doubleArrayOf(
            0.5, 0.0, 0.5, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.5, 0.0, 0.5, 0.0,
            0.0, 0.0, 0.0, 0.0,
        )
    )

    val LinearPolarizerMinus45 = Matrix(
        4, 4,
        doubleArrayOf(
            0.5, 0.0, -0.5, 0.0,
            0.0, 0.0, 0.0, 0.0,
            -0.5, 0.0, 0.5, 0.0,
            0.0, 0.0, 0.0, 0.0,
        )
    )

    val QuarterWavePlateFastAxisVertical = Matrix(
        4, 4,
        doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 0.0, -1.0,
            0.0, 0.0, 1.0, 0.0,
        )
    )

    val QuarterWavePlateFastAxisHorizontal = Matrix(
        4, 4,
        doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 1.0,
            0.0, 0.0, -1.0, 0.0,
        )
    )

    val HalfWavePlate = Matrix(
        4, 4,
        doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, -1.0, 0.0,
            0.0, 0.0, 0.0, -1.0,
        )
    )

    fun forReferenceFrameRotation(radians: Double): Matrix {
        val cos2Theta = cos(2.0 * radians)
        val sin2Theta = sin(2.0 * radians)
        return Matrix(
            4, 4,
            doubleArrayOf(
                1.0, 0.0, 0.0, 0.0,
                0.0, cos2Theta, sin2Theta, 0.0,
                0.0, -sin2Theta, cos2Theta, 0.0,
                0.0, 0.0, 0.0, 1.0,
            )
        )
    }

    fun forLinearRetarder(fastAxisRadians: Double, phaseDifferenceRadians: Double): Matrix {
        val cos2Theta = cos(2.0 * fastAxisRadians)
        val sin2Theta = sin(2.0 * fastAxisRadians)
        val cos22Theta = cos2Theta * cos2Theta
        val sin22Theta = sin2Theta * sin2Theta
        val cosDelta = cos(phaseDifferenceRadians)
        val sinDelta = sin(phaseDifferenceRadians)
        return Matrix(
            4, 4,
            doubleArrayOf(
                1.0, 0.0, 0.0, 0.0,
                0.0, cos22Theta + sin22Theta * cosDelta, cos2Theta * sin2Theta * (1 - cosDelta), sin2Theta * sinDelta,
                0.0, cos2Theta * sin2Theta * (1 - cosDelta), cos22Theta * cosDelta + sin22Theta, -cos2Theta * sinDelta,
                0.0, -sin2Theta * sinDelta, cos2Theta * sinDelta, cosDelta,
            )
        )
    }
}
