package garden.ephemeral.rocket.importers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import garden.ephemeral.rocket.FileStepDefinitions
import garden.ephemeral.rocket.MaterialStepDefinitions
import io.cucumber.java8.En

class MtlFileStepDefinitions : En {
    private lateinit var parser: MtlFileParser

    init {
        When("parser ← parse_mtl_file\\({file_var})") { fv: String ->
            parser = MtlFileParser(FileStepDefinitions.files[fv]!!)

            // Define material so that subsequent steps can use existing definitions for materials
            MaterialStepDefinitions.material = parser.materials.values.iterator().next()
        }

        Then("parse_mtl_file\\({file_var}) should fail") { fv: String ->
            assertThat { MtlFileParser(FileStepDefinitions.files[fv]!!) }.isFailure()
                .isInstanceOf(MtlFileParser.InvalidMtlException::class.java)
        }

        Then("parser.materials.size = {int}") { expected: Int ->
            assertThat(parser.materials.size).isEqualTo(expected)
        }
    }
}
