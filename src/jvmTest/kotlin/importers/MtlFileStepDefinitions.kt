package garden.ephemeral.rocket.importers

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import garden.ephemeral.rocket.Space
import io.cucumber.java8.En
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem

// Constructed reflectively
@Suppress("unused")
class MtlFileStepDefinitions(space: Space) : En {
    private lateinit var parser: MtlFileParser

    init {
        When("parser ← parse_mtl_file\\({file_var})") { fv: String ->
            parser = SystemFileSystem.source(space.files[fv]!!).use { source ->
                MtlFileParser(source.buffered())
            }

            // Define material so that subsequent steps can use existing definitions for materials
            space.material = parser.materials.values.iterator().next()
        }

        Then("parse_mtl_file\\({file_var}) should fail") { fv: String ->
            assertThat {
                SystemFileSystem.source(space.files[fv]!!).use { source ->
                    MtlFileParser(source.buffered())
                }
            }.isFailure()
                .isInstanceOf(MtlFileParser.InvalidMtlException::class.java)
        }

        Then("parser.materials.size = {int}") { expected: Int ->
            assertThat(parser.materials.size).isEqualTo(expected)
        }
    }
}
