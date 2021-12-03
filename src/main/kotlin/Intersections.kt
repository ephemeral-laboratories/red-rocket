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
     * Uses the fact that both lists are already in order to optimise the number of
     * elements which need to be checked.
     *
     * @param other the other intersections.
     * @return the merged intersections.
     */
    fun merge(other: Intersections): Intersections {
        var indexLeft = 0
        var indexRight = 0

        return Intersections(
            buildList {
                while (indexLeft < this@Intersections.size && indexRight < other.size) {
                    val left = this@Intersections[indexLeft]
                    val right = other[indexRight]
                    if (left.t <= right.t) {
                        add(left)
                        indexLeft++
                    } else {
                        add(right)
                        indexRight++
                    }
                }

                while (indexLeft < this@Intersections.size) {
                    add(this@Intersections[indexLeft])
                    indexLeft++
                }

                while (indexRight < other.size) {
                    add(other[indexRight])
                    indexRight++
                }
            }
        )
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
