package garden.ephemeral.rocket

data class Ray(val origin: Tuple, val direction: Tuple) {
    fun position(t: Double): Tuple {
        return origin + direction * t
    }

    fun transform(matrix: Matrix): Ray {
        return Ray(matrix * origin, matrix * direction)
    }
}