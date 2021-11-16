package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.color.Color
import java.util.*
import kotlin.math.abs

class CubeMap(left: UVPattern, front: UVPattern, right: UVPattern, back: UVPattern, up: UVPattern, down: UVPattern) :
    Pattern() {

    private val faceUvPatterns: Map<Face, UVPattern> = mapOf(
        Face.LEFT to left,
        Face.FRONT to front,
        Face.RIGHT to right,
        Face.BACK to back,
        Face.UP to up,
        Face.DOWN to down
    )

    override fun patternAt(patternPoint: Tuple): Color {
        val face = faceFromPoint(patternPoint)
        val (u, v) = (faceUvMaps[face] ?: error("Missing face UV map entry"))(patternPoint)
        return (faceUvPatterns[face] ?: error("Missing face UV pattern entry")).uvPatternAt(u, v)
    }

    companion object {
        fun faceFromPoint(point: Tuple): Face {
            return when (maxOf(abs(point.x), abs(point.y), abs(point.z))) {
                point.x -> Face.RIGHT
                -point.x -> Face.LEFT
                point.y -> Face.UP
                -point.y -> Face.DOWN
                point.z -> Face.FRONT
                else -> Face.BACK
            }
        }

        val cubeUvRight: UVMap = { point: Tuple ->
            val u = ((1 - point.z) fmod 2.0) / 2.0
            val v = ((point.y + 1) fmod 2.0) / 2.0
            UV(u, v)
        }

        val cubeUvLeft: UVMap = { point: Tuple ->
            val u = ((point.z + 1) fmod 2.0) / 2.0
            val v = ((point.y + 1) fmod 2.0) / 2.0
            UV(u, v)
        }

        val cubeUvUp: UVMap = { point: Tuple ->
            val u = ((point.x + 1) fmod 2.0) / 2.0
            val v = ((1 - point.z) fmod 2.0) / 2.0
            UV(u, v)
        }

        val cubeUvDown: UVMap = { point: Tuple ->
            val u = ((point.x + 1) fmod 2.0) / 2.0
            val v = ((point.z + 1) fmod 2.0) / 2.0
            UV(u, v)
        }

        val cubeUvFront: UVMap = { point: Tuple ->
            val u = ((point.x + 1) fmod 2.0) / 2.0
            val v = ((point.y + 1) fmod 2.0) / 2.0
            UV(u, v)
        }

        val cubeUvBack: UVMap = { point: Tuple ->
            val u = ((1 - point.x) fmod 2.0) / 2.0
            val v = ((point.y + 1) fmod 2.0) / 2.0
            UV(u, v)
        }

        private val faceUvMaps: Map<Face, UVMap> = mapOf(
            Face.LEFT to cubeUvLeft,
            Face.FRONT to cubeUvFront,
            Face.RIGHT to cubeUvRight,
            Face.BACK to cubeUvBack,
            Face.UP to cubeUvUp,
            Face.DOWN to cubeUvDown
        )
    }

    enum class Face {
        RIGHT, LEFT, UP, DOWN, FRONT, BACK;

        companion object {
            fun fromString(string: String): Face {
                return valueOf(string.uppercase(Locale.ROOT))
            }
        }
    }
}
