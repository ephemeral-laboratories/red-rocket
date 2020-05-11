class Intersection(val t: Double, val obj: Sphere) {
    companion object {
        fun hit(intersections: List<Intersection>): Intersection? {
            return intersections
                .filter { intersection -> intersection.t > 0 }
                .minBy { intersection -> intersection.t }
        }
    }
}