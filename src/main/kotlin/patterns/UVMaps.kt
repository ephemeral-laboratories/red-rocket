package garden.ephemeral.rocket.patterns

import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.vector
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2

class UVMaps {
    companion object {
        val planarMap: (Tuple) -> UV = { point: Tuple ->
            val u = point.x fmod 1.0
            val v = point.z fmod 1.0
            UV(u, v)
        }

        val sphericalMap: (Tuple) -> UV = { point: Tuple ->
            val theta = atan2(point.x, point.z)
            val vec = vector(point.x, point.y, point.z)
            val radius = vec.magnitude
            val phi = acos(point.y / radius)
            val rawU = theta / (2 * PI)
            val u = 1 - (rawU + 0.5)
            val v = 1 - phi / PI
            UV(u, v)
        }

        val cylindricalMap: (Tuple) -> UV = { point: Tuple ->
            val theta = atan2(point.x, point.z)
            val rawU = theta / (2 * PI)
            val u = 1 - (rawU + 0.5)
            val v = point.y fmod 1.0
            UV(u, v)
        }
    }
}

infix fun Double.fmod(other: Double): Double {
    return ((this % other) + other) % other
}
