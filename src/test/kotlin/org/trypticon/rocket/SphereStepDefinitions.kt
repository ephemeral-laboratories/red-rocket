
import MaterialStepDefinitions.Companion.m
import MatrixStepDefinitions.Companion.matrices
import TupleStepDefinitions.Companion.tuples
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java8.En

class SphereStepDefinitions: En {
    private val epsilon: Double = 0.00001

    companion object {
        lateinit var s: Sphere
    }

    init {
        Given("^s ← sphere\\(\\)") {
            s = Sphere()
        }

        When("set_transform\\(s, {matrix_var})") { mv: String ->
            s.transform = matrices[mv]!!
        }
        When("set_transform\\(s, {translation})") { m: Matrix ->
            s.transform = m
        }
        When("set_transform\\(s, {scaling})") { m: Matrix ->
            s.transform = m
        }

        When("{tuple_var} ← normal_at\\(s, {point})") { tv: String, p: Tuple ->
            tuples[tv] = s.worldNormalAt(p)
        }

        When("m ← s.material") {
            m = s.material
        }
        When("s.material ← m") {
            s.material = m
        }

        Then("s.transform = {matrix_var}") { mv: String ->
            assertThat(s.transform).isEqualTo(matrices[mv])
        }

        Then("s.material = m") {
            assertThat(s.material).isEqualTo(m)
        }
    }
}