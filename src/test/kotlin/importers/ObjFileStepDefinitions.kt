package garden.ephemeral.rocket.importers

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.FileStepDefinitions.Companion.files
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.shapes.BaseTriangle
import garden.ephemeral.rocket.shapes.Group
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import garden.ephemeral.rocket.shapes.SmoothTriangle
import io.cucumber.java8.En

class ObjFileStepDefinitions: En {
    private lateinit var parser: ObjFileParser

    init {
        When("parser ← parse_obj_file\\({file_var})") { fv: String ->
            parser = ObjFileParser(files[fv]!!)
        }

        When("{shape_var} ← parser.default_group") { sv: String ->
            shapes[sv] = parser.defaultGroup
        }
        When("{shape_var} ← {string} from parser") { sv: String, string: String ->
            shapes[sv] = parser.namedGroup(string)!!
        }

        When("{shape_var} ← obj_to_group\\(parser)") { sv: String ->
            shapes[sv] = parser.objToGroup()
        }

        Then("parser should have ignored {int} lines") { i: Int ->
            assertThat(parser.ignoredLines).isEqualTo(i)
        }

        Then("parser.vertices[{int}] = {point}") { i: Int, e: Tuple ->
            assertThat(parser.vertex(i)).isEqualTo(e)
        }
        Then("parser.normals[{int}] = {vector}") { i: Int, e: Tuple ->
            assertThat(parser.normal(i)).isEqualTo(e)
        }

        Then("{shape_var}.point1 = parser.vertices[{int}]") { sv: String, i: Int ->
            assertThat((shapes[sv] as BaseTriangle).point1).isEqualTo(parser.vertex(i))
        }
        Then("{shape_var}.point2 = parser.vertices[{int}]") { sv: String, i: Int ->
            assertThat((shapes[sv] as BaseTriangle).point2).isEqualTo(parser.vertex(i))
        }
        Then("{shape_var}.point3 = parser.vertices[{int}]") { sv: String, i: Int ->
            assertThat((shapes[sv] as BaseTriangle).point3).isEqualTo(parser.vertex(i))
        }
        Then("{shape_var}.normal1 = parser.normals[{int}]") { sv: String, i: Int ->
            assertThat((shapes[sv] as SmoothTriangle).normal1).isEqualTo(parser.normal(i))
        }
        Then("{shape_var}.normal2 = parser.normals[{int}]") { sv: String, i: Int ->
            assertThat((shapes[sv] as SmoothTriangle).normal2).isEqualTo(parser.normal(i))
        }
        Then("{shape_var}.normal3 = parser.normals[{int}]") { sv: String, i: Int ->
            assertThat((shapes[sv] as SmoothTriangle).normal3).isEqualTo(parser.normal(i))
        }

        Then("{shape_var} includes {string} from parser") { sv: String, string: String ->
            assertThat((shapes[sv] as Group).children).contains(parser.namedGroup(string))
        }
    }
}