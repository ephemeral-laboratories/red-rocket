package garden.ephemeral.rocket

import garden.ephemeral.rocket.util.Angle
import garden.ephemeral.rocket.util.cos
import garden.ephemeral.rocket.util.sin
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray

class Transforms {
    companion object {
        fun translation(x: Double, y: Double, z: Double): Matrix {
            return Matrix(
                mk.ndarray(
                    mk[
                        mk[1.0, 0.0, 0.0, x],
                        mk[0.0, 1.0, 0.0, y],
                        mk[0.0, 0.0, 1.0, z],
                        mk[0.0, 0.0, 0.0, 1.0],
                    ]
                )
            )
        }

        fun scaling(x: Double, y: Double, z: Double): Matrix {
            return Matrix(
                mk.ndarray(
                    mk[
                        mk[x, 0.0, 0.0, 0.0],
                        mk[0.0, y, 0.0, 0.0],
                        mk[0.0, 0.0, z, 0.0],
                        mk[0.0, 0.0, 0.0, 1.0],
                    ]
                )
            )
        }

        fun rotationX(theta: Angle): Matrix {
            val cosR = cos(theta)
            val sinR = sin(theta)
            return Matrix(
                mk.ndarray(
                    mk[
                        mk[1.0, 0.0, 0.0, 0.0],
                        mk[0.0, cosR, -sinR, 0.0],
                        mk[0.0, sinR, cosR, 0.0],
                        mk[0.0, 0.0, 0.0, 1.0],
                    ]
                )
            )
        }

        fun rotationY(theta: Angle): Matrix {
            val cosR = cos(theta)
            val sinR = sin(theta)
            return Matrix(
                mk.ndarray(
                    mk[
                        mk[cosR, 0.0, sinR, 0.0],
                        mk[0.0, 1.0, 0.0, 0.0],
                        mk[-sinR, 0.0, cosR, 0.0],
                        mk[0.0, 0.0, 0.0, 1.0],
                    ]
                )
            )
        }

        fun rotationZ(theta: Angle): Matrix {
            val cosR = cos(theta)
            val sinR = sin(theta)
            return Matrix(
                mk.ndarray(
                    mk[
                        mk[cosR, -sinR, 0.0, 0.0],
                        mk[sinR, cosR, 0.0, 0.0],
                        mk[0.0, 0.0, 1.0, 0.0],
                        mk[0.0, 0.0, 0.0, 1.0],
                    ],
                )
            )
        }

        fun shearing(xy: Double, xz: Double, yx: Double, yz: Double, zx: Double, zy: Double): Matrix {
            return Matrix(
                mk.ndarray(
                    mk[
                        mk[1.0, xy, xz, 0.0],
                        mk[yx, 1.0, yz, 0.0],
                        mk[zx, zy, 1.0, 0.0],
                        mk[0.0, 0.0, 0.0, 1.0],
                    ]
                )
            )
        }

        fun viewTransform(from: Tuple, to: Tuple, up: Tuple): Matrix {
            val forward = (to - from).normalize()
            val left = forward.cross(up.normalize())
            val trueUp = left.cross(forward)
            val orientation = Matrix(
                mk.ndarray(
                    mk[
                        mk[left.x, left.y, left.z, 0.0],
                        mk[trueUp.x, trueUp.y, trueUp.z, 0.0],
                        mk[-forward.x, -forward.y, -forward.z, 0.0],
                        mk[0.0, 0.0, 0.0, 1.0],
                    ]
                )
            )
            return orientation * translation(-from.x, -from.y, -from.z)
        }
    }
}
