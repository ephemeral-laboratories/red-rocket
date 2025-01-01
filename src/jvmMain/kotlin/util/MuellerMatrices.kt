package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Matrix

object MuellerMatrices {
    val LinearPolarizerHorizontal = Matrix(
        4,
        4,
        immutableDoubleArrayOf(
            0.5, 0.5, 0.0, 0.0,
            0.5, 0.5, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0
        )
    )

    val LinearPolarizerVertical = Matrix(
        4,
        4,
        immutableDoubleArrayOf(
            0.5, -0.5, 0.0, 0.0,
            -0.5, 0.5, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0
        )
    )

    val LinearPolarizerPlus45 = Matrix(
        4,
        4,
        immutableDoubleArrayOf(
            0.5, 0.0, 0.5, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.5, 0.0, 0.5, 0.0,
            0.0, 0.0, 0.0, 0.0
        )
    )

    val LinearPolarizerMinus45 = Matrix(
        4,
        4,
        immutableDoubleArrayOf(
            0.5, 0.0, -0.5, 0.0,
            0.0, 0.0, 0.0, 0.0,
            -0.5, 0.0, 0.5, 0.0,
            0.0, 0.0, 0.0, 0.0
        )
    )

    val QuarterWavePlateFastAxisVertical = Matrix(
        4,
        4,
        immutableDoubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 0.0, -1.0,
            0.0, 0.0, 1.0, 0.0
        )
    )

    val QuarterWavePlateFastAxisHorizontal = Matrix(
        4,
        4,
        immutableDoubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 1.0,
            0.0, 0.0, -1.0, 0.0
        )
    )

    val HalfWavePlate = Matrix(
        4,
        4,
        immutableDoubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, -1.0, 0.0,
            0.0, 0.0, 0.0, -1.0
        )
    )

    fun forReferenceFrameRotation(angle: Angle): Matrix {
        val cos2Theta = cos(2.0 * angle)
        val sin2Theta = sin(2.0 * angle)
        return Matrix(
            4,
            4,
            immutableDoubleArrayOf(
                1.0, 0.0, 0.0, 0.0,
                0.0, cos2Theta, sin2Theta, 0.0,
                0.0, -sin2Theta, cos2Theta, 0.0,
                0.0, 0.0, 0.0, 1.0
            )
        )
    }

    fun forLinearRetarder(fastAxis: Angle, phaseDifference: Angle): Matrix {
        val cos2Theta = cos(2.0 * fastAxis)
        val sin2Theta = sin(2.0 * fastAxis)
        val cos22Theta = cos2Theta * cos2Theta
        val sin22Theta = sin2Theta * sin2Theta
        val cosDelta = cos(phaseDifference)
        val sinDelta = sin(phaseDifference)
        return Matrix(
            4,
            4,
            immutableDoubleArrayOf(
                1.0, 0.0, 0.0, 0.0,
                0.0, cos22Theta + sin22Theta * cosDelta, cos2Theta * sin2Theta * (1 - cosDelta), sin2Theta * sinDelta,
                0.0, cos2Theta * sin2Theta * (1 - cosDelta), cos22Theta * cosDelta + sin22Theta, -cos2Theta * sinDelta,
                0.0, -sin2Theta * sinDelta, cos2Theta * sinDelta, cosDelta
            )
        )
    }
}
