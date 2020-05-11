
import MatrixStepDefinitions.Companion.matrices
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

        Then("s.transform = {matrix_var}") { mv: String ->
            assertThat(s.transform).isEqualTo(matrices[mv])
        }
    }
}