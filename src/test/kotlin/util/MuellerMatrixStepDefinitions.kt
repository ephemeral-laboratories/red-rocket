package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.MatrixStepDefinitions.Companion.matrices
import io.cucumber.java8.En

class MuellerMatrixStepDefinitions: En {
    init {
        val mappings = mapOf(
            "linear polarizer \\(horizontal transmission)" to MuellerMatrices.LinearPolarizerHorizontal,
            "linear polarizer \\(vertical transmission)" to MuellerMatrices.LinearPolarizerVertical,
            "linear polarizer \\(+45° transmission)" to MuellerMatrices.LinearPolarizerPlus45,
            "linear polarizer \\(-45° transmission)" to MuellerMatrices.LinearPolarizerMinus45,
            "quarter-wave plate \\(fast-axis vertical)" to MuellerMatrices.QuarterWavePlateFastAxisVertical,
            "quarter-wave plate \\(fast-axis horizontal)" to MuellerMatrices.QuarterWavePlateFastAxisHorizontal,
            "half-wave plate" to MuellerMatrices.HalfWavePlate,
        )

        mappings.forEach { (thing, matrix) ->
            When("{matrix_var} is a Mueller matrix for a $thing") { matrixVar: String ->
                matrices[matrixVar] = matrix
            }
        }
    }
}