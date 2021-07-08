package garden.ephemeral.rocket.patterns

import assertk.assertThat
import assertk.assertions.isEqualTo
import garden.ephemeral.rocket.CanvasStepDefinitions.Companion.canvas
import garden.ephemeral.rocket.Color
import garden.ephemeral.rocket.ColorStepDefinitions.Companion.colors
import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.MatrixStepDefinitions.Companion.matrices
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.TupleStepDefinitions.Companion.tuples
import garden.ephemeral.rocket.shapes.ShapeStepDefinitions.Companion.shapes
import io.cucumber.java8.En

class PatternStepDefinitions: En {
    companion object {
        lateinit var pattern: Pattern
        val uvPatterns: MutableMap<String, UVPattern> = mutableMapOf()
        lateinit var uv: UV
        lateinit var face: CubeMap.Face
    }

    init {
        ParameterType("uv_pattern_var", "uv_\\w+") { string -> string }

        Given("^pattern ← test_pattern\\(\\)") { pattern =
            TestPattern()
        }

        Given("pattern ← stripe_pattern\\({color_var}, {color_var})") { cv1: String, cv2: String ->
            pattern = StripePattern(colors[cv1]!!, colors[cv2]!!)
        }
        Given("pattern ← gradient_pattern\\({color_var}, {color_var})") { cv1: String, cv2: String ->
            pattern = GradientPattern(colors[cv1]!!, colors[cv2]!!)
        }
        Given("pattern ← ring_pattern\\({color_var}, {color_var})") { cv1: String, cv2: String ->
            pattern = RingPattern(colors[cv1]!!, colors[cv2]!!)
        }
        Given("pattern ← checkers_pattern\\({color_var}, {color_var})") { cv1: String, cv2: String ->
            pattern = CheckersPattern(colors[cv1]!!, colors[cv2]!!)
        }
        Given("pattern ← texture_map\\({uv_pattern_var}, spherical_map)") { upv: String ->
            pattern = TextureMap(uvPatterns[upv]!!, UVMaps.sphericalMap)
        }
        Given("pattern ← cube_map\\({uv_pattern_var}, {uv_pattern_var}, {uv_pattern_var}, " +
                "{uv_pattern_var}, {uv_pattern_var}, {uv_pattern_var})") {
                upv1: String, upv2: String, upv3: String, upv4: String, upv5: String, upv6: String ->
            pattern = CubeMap(uvPatterns[upv1]!!, uvPatterns[upv2]!!, uvPatterns[upv3]!!,
                uvPatterns[upv4]!!, uvPatterns[upv5]!!, uvPatterns[upv6]!!)
        }

        Given("{uv_pattern_var} ← uv_align_check\\({color_var}, {color_var}, {color_var}, {color_var}, {color_var})") {
                upv: String, cv1: String, cv2: String, cv3: String, cv4: String, cv5: String ->
            uvPatterns[upv] = UVAlignCheck(colors[cv1]!!, colors[cv2]!!, colors[cv3]!!, colors[cv4]!!, colors[cv5]!!)
        }
        Given("{uv_pattern_var} ← uv_checkers\\({int}, {int}, {color_var}, {color_var})") {
                upv: String, width: Int, height: Int, cv1: String, cv2: String ->
            uvPatterns[upv] = UVCheckers(width, height, colors[cv1]!!, colors[cv2]!!)
        }
        Given("{uv_pattern_var} ← uv_image\\(canvas)") { upv: String ->
            uvPatterns[upv] = UVImage(canvas)
        }

        Given("set_pattern_transform\\(pattern, {transform})") { t: Matrix ->
            pattern.transform = t
        }

        When("{color_var} ← stripe_at_object\\(pattern, {shape_var}, {point})") { cv: String, sv: String, p: Tuple ->
            colors[cv] = pattern.patternAtShape(shapes[sv]!!, p)
        }
        When("{color_var} ← pattern_at_shape\\(pattern, {shape_var}, {point})") { cv: String, sv: String, p: Tuple ->
            colors[cv] = pattern.patternAtShape(shapes[sv]!!, p)
        }

        When("{color_var} ← uv_pattern_at\\({uv_pattern_var}, {real}, {real})") {
                cv: String, upv: String, u: Double, v: Double ->
            colors[cv] = uvPatterns[upv]!!.uvPatternAt(u, v)
        }

        When("\\(u, v) ← spherical_map\\({tuple_var})") { tv: String ->
            uv = UVMaps.sphericalMap(tuples[tv]!!)
        }
        When("\\(u, v) ← planar_map\\({tuple_var})") { tv: String ->
            uv = UVMaps.planarMap(tuples[tv]!!)
        }
        When("\\(u, v) ← cylindrical_map\\({tuple_var})") { tv: String ->
            uv = UVMaps.cylindricalMap(tuples[tv]!!)
        }

        When("face ← face_from_point\\({point})") { p: Tuple ->
            face = CubeMap.faceFromPoint(p)
        }

        When("\\(u, v) ← cube_uv_right\\({point})") { p: Tuple ->
            uv = CubeMap.cubeUvRight(p)
        }
        When("\\(u, v) ← cube_uv_left\\({point})") { p: Tuple ->
            uv = CubeMap.cubeUvLeft(p)
        }
        When("\\(u, v) ← cube_uv_up\\({point})") { p: Tuple ->
            uv = CubeMap.cubeUvUp(p)
        }
        When("\\(u, v) ← cube_uv_down\\({point})") { p: Tuple ->
            uv = CubeMap.cubeUvDown(p)
        }
        When("\\(u, v) ← cube_uv_front\\({point})") { p: Tuple ->
            uv = CubeMap.cubeUvFront(p)
        }
        When("\\(u, v) ← cube_uv_back\\({point})") { p: Tuple ->
            uv = CubeMap.cubeUvBack(p)
        }

        Then("pattern.transform = {transform}") { t: Matrix -> assertThat(pattern.transform).isEqualTo(t) }

        Then("pattern.transform = {matrix_var}") { mv: String ->
            assertThat(pattern.transform).isEqualTo(matrices[mv])
        }

        Then("pattern.a = {color_var}") { tv: String ->
            assertThat((pattern as StripePattern).a).isEqualTo(colors[tv]!!)
        }
        Then("pattern.b = {color_var}") { tv: String ->
            assertThat((pattern as StripePattern).b).isEqualTo(colors[tv]!!)
        }

        Then("stripe_at\\(pattern, {point}) = {color_var}") { p: Tuple, tv: String ->
            assertThat(pattern.patternAt(p)).isEqualTo(colors[tv]!!)
        }
        Then("pattern_at\\(pattern, {point}) = {color}") { p: Tuple, e: Color ->
            assertThat(pattern.patternAt(p)).isEqualTo(e)
        }
        Then("pattern_at\\(pattern, {point}) = {color_var}") { p: Tuple, tv: String ->
            assertThat(pattern.patternAt(p)).isEqualTo(colors[tv]!!)
        }

        Then("u = {real}") { e: Double ->
            assertThat(uv.u).isEqualTo(e)
        }
        Then("v = {real}") { e: Double ->
            assertThat(uv.v).isEqualTo(e)
        }

        Then("face = {string}") { string: String ->
            assertThat(face).isEqualTo(CubeMap.Face.fromString(string))
        }
    }
}