package garden.ephemeral.rocket

import kotlin.math.cos
import kotlin.math.sin

class Transforms {
    companion object {
        fun translation(x: Double, y: Double, z: Double): Matrix {
            return Matrix(4, 4, doubleArrayOf(
                1.0, 0.0, 0.0, x,
                0.0, 1.0, 0.0, y,
                0.0, 0.0, 1.0, z,
                0.0, 0.0, 0.0, 1.0))
        }

        fun scaling(x: Double, y: Double, z: Double): Matrix {
            return Matrix(4, 4, doubleArrayOf(
                x, 0.0, 0.0, 0.0,
                0.0, y, 0.0, 0.0,
                0.0, 0.0, z, 0.0,
                0.0, 0.0, 0.0, 1.0))
        }

        fun rotationX(thetaRadians: Double): Matrix {
            val cosR = cos(thetaRadians)
            val sinR = sin(thetaRadians)
            return Matrix(4, 4, doubleArrayOf(
                1.0, 0.0, 0.0, 0.0,
                0.0, cosR, -sinR, 0.0,
                0.0, sinR, cosR, 0.0,
                0.0, 0.0, 0.0, 1.0))
        }

        fun rotationY(thetaRadians: Double): Matrix {
            val cosR = cos(thetaRadians)
            val sinR = sin(thetaRadians)
            return Matrix(4, 4, doubleArrayOf(
                cosR, 0.0, sinR, 0.0,
                0.0, 1.0, 0.0, 0.0,
                -sinR, 0.0, cosR, 0.0,
                0.0, 0.0, 0.0, 1.0))
        }

        fun rotationZ(thetaRadians: Double): Matrix {
            val cosR = cos(thetaRadians)
            val sinR = sin(thetaRadians)
            return Matrix(4, 4, doubleArrayOf(
                cosR, -sinR, 0.0, 0.0,
                sinR, cosR, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0))
        }

        fun shearing(xy: Double, xz: Double, yx: Double, yz: Double, zx: Double, zy: Double): Matrix {
            return Matrix(4, 4, doubleArrayOf(
                1.0, xy, xz, 0.0,
                yx, 1.0, yz, 0.0,
                zx, zy, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0))
        }

        fun viewTransform(from: Tuple, to: Tuple, up: Tuple): Matrix {
            val forward = (to - from).normalize()
            val upn = up.normalize()
            val left = forward.cross(upn)
            val trueUp = left.cross(forward)
            val orientation = Matrix(4, 4, doubleArrayOf(
                    left.x,     left.y,     left.z, 0.0,
                  trueUp.x,   trueUp.y,   trueUp.z, 0.0,
                -forward.x, -forward.y, -forward.z, 0.0,
                       0.0,        0.0,        0.0, 1.0))
            return orientation * translation(-from.x, -from.y, -from.z)
        }
    }
}