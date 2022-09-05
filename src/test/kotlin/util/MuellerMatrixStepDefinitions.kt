package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.Universe
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class MuellerMatrixStepDefinitions(universe: Universe) : En {
    init {
        val mappings = mapOf(
            "linear polarizer \\(horizontal transmission)" to MuellerMatrices.LinearPolarizerHorizontal,
            "linear polarizer \\(vertical transmission)" to MuellerMatrices.LinearPolarizerVertical,
            "linear polarizer \\(+45° transmission)" to MuellerMatrices.LinearPolarizerPlus45,
            "linear polarizer \\(-45° transmission)" to MuellerMatrices.LinearPolarizerMinus45,
            "quarter-wave plate \\(fast-axis vertical)" to MuellerMatrices.QuarterWavePlateFastAxisVertical,
            "quarter-wave plate \\(fast-axis horizontal)" to MuellerMatrices.QuarterWavePlateFastAxisHorizontal,
            "half-wave plate" to MuellerMatrices.HalfWavePlate
        )

        mappings.forEach { (thing, matrix) ->
            When("{matrix_var} is a Mueller matrix for a $thing") { matrixVar: String ->
                universe.matrices[matrixVar] = matrix
            }

            When("{tuple_var} passes through a $thing") { tupleVar: String ->
                universe.tuples[tupleVar] = matrix * universe.tuples[tupleVar]!!
            }
        }

        When("{matrix_var} is a Mueller matrix for a reference rotation of {real} degrees") {
                matrixVar: String, degrees: Double ->
            universe.matrices[matrixVar] = MuellerMatrices.forReferenceFrameRotation(degrees.deg)
        }

        When(
            "{matrix_var} is a Mueller matrix for a linear retarder " +
                "with fast axis {real} degrees " +
                "and phase difference {real} degrees"
        ) { matrixVar: String, fastAxisDegrees: Double, phaseDifferenceDegrees: Double ->
            universe.matrices[matrixVar] =
                MuellerMatrices.forLinearRetarder(fastAxisDegrees.deg, phaseDifferenceDegrees.deg)
        }
    }
}
