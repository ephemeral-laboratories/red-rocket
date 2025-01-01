package garden.ephemeral.rocket.importers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import garden.ephemeral.rocket.Space
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class MtlFileStepDefinitions(space: Space) : En {
    private lateinit var parser: MtlFileParser

    init {
        When("parser ← parse_mtl_file\\({file_var})") { fv: String ->
            parser = MtlFileParser(space.files[fv]!!)

            // Define material so that subsequent steps can use existing definitions for materials
            space.material = parser.materials.values.iterator().next()
        }

        Then("parse_mtl_file\\({file_var}) should fail") { fv: String ->
            assertThat { MtlFileParser(space.files[fv]!!) }.isFailure()
                .isInstanceOf(MtlFileParser.InvalidMtlException::class.java)
        }

        Then("parser.materials.size = {int}") { expected: Int ->
            assertThat(parser.materials.size).isEqualTo(expected)
        }
    }
}
