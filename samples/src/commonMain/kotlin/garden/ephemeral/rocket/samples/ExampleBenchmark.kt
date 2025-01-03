package garden.ephemeral.rocket.samples

import kotlinx.benchmark.Benchmark
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
import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.operations.sum

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@State(Scope.Benchmark)
class ExampleBenchmark {

    @Param("DEFAULT", "KOTLIN", "NATIVE")
    var engineType: String = ""

    // Parameterizes the benchmark to run with different list sizes
    @Param("4", "10", "100", "1000")
    var size: Int = 0

    private lateinit var array: D1Array<Int>

    // Prepares the test environment before each benchmark run
    @Setup
    fun prepare() {
        mk.setEngine(mk.engines[engineType]!!)
        array = mk.d1array(size) { i -> i }
    }

    // The actual benchmark method
    @Benchmark
    fun benchmarkMethod(): Int {
        return array.sum()
    }
}
