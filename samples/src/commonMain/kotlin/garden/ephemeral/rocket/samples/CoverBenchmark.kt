package garden.ephemeral.rocket.samples

import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import org.jetbrains.kotlinx.multik.api.mk

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
class CoverBenchmark {

    @Param("DEFAULT", "KOTLIN", "NATIVE")
    var engineType: String = ""

    // Prepares the test environment before each benchmark run
    @Setup
    fun prepare() {
        mk.setEngine(mk.engines[engineType]!!)
    }

    // TODO: Make it possible to run Cover example from commonMain
//    @Benchmark
//    fun benchmarkMethod() {
//         CoverKt.main()
//    }
}