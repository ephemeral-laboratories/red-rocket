package garden.ephemeral.rocket

/**
 * Collection of intersections.
 *
 * @constructor constructs the collection of intersections. The intersections must
 *              already be sorted!
 */
class Intersections(private val intersections: List<Intersection>) : List<Intersection> {
    /**
     * Convenience constructor to construct the collection of intersections from varargs.
     * The intersections must already be sorted!
     */
    constructor(vararg intersections: Intersection) : this(listOf(*intersections))

    /**
     * Gets the first hit in the intersections.
     * That is, the first intersection with a positive `t` value.
     *
     * @return the hit, if any.
     */
    fun hit(): Intersection? {
        return intersections
            .firstOrNull { intersection -> intersection.t > 0 }
    }

    /**
     * Merges these intersections with another.
     *
     * @param other the other intersections.
     * @return the merged intersections.
     */
    fun merge(other: Intersections): Intersections {
        // XXX: Could potentially optimise since we know both sides are already sorted,
        //      but is there a Kotlin-friendly way to do this?
        return asSequence()
            .plus(other)
            .toIntersections()
    }

    // Delegate a bunch of stuff to `intersections`
    override val size: Int = intersections.size
    override fun isEmpty(): Boolean = intersections.isEmpty()
    override fun get(index: Int): Intersection = intersections[index]
    override fun contains(element: Intersection): Boolean = intersections.contains(element)
    override fun containsAll(elements: Collection<Intersection>): Boolean = intersections.containsAll(elements)
    override fun indexOf(element: Intersection): Int = intersections.indexOf(element)
    override fun lastIndexOf(element: Intersection): Int = intersections.lastIndexOf(element)
    override fun listIterator(): ListIterator<Intersection> = intersections.listIterator()
    override fun listIterator(index: Int): ListIterator<Intersection> = intersections.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int): List<Intersection> =
        Intersections(intersections.subList(fromIndex, toIndex))

    override fun iterator(): Iterator<Intersection> = intersections.iterator()

    companion object {
        /**
         * Convenience constant to use for the case of no intersections.
         */
        val None = Intersections()
    }
}

/**
 * Extension to a sequence of intersections to collect the sequence into an `Intersections`.
 * Takes care of sorting the sequence first.
 *
 * @receiver the sequence of intersections.
 * @return the intersections.
 */
fun Sequence<Intersection>.toIntersections(): Intersections {
    return Intersections(
        sortedBy { i -> i.t }
            .toList()
    )
}
