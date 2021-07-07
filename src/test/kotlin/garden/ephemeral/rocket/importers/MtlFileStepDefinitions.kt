package garden.ephemeral.rocket.importers

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.FileStepDefinitions
import io.cucumber.java8.En

class MtlFileStepDefinitions: En {
    lateinit var parser: MtlFileParser

    init {
        When("parser ← parse_mtl_file\\({file_var})") { fv: String ->
            parser = MtlFileParser(FileStepDefinitions.files[fv]!!)
        }

        Then("parser.materials.size = {int}") { expected: Int ->
            assertThat(parser.materials.size).isEqualTo(expected)
        }
    }
}