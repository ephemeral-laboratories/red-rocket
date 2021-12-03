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

/**
 * Merges multiple lists of intersections.
 *
 * Uses the fact that both lists are already in order to optimise the number of
 * elements which need to be checked.
 *
 * @param multiple the lists of intersections to merge.
 * @return the merged list of intersections.
 */
fun merge(vararg multiple: Intersections): Intersections {
    return merge(listOf(*multiple))
}

/**
 * Merges multiple lists of intersections.
 *
 * Uses the fact that both lists are already in order to optimise the number of
 * elements which need to be checked.
 *
 * @param multiple the lists of intersections to merge.
 * @return the merged list of intersections.
 */
fun merge(multiple: Iterable<Intersections>): Intersections {
    class Candidate(val iterator: Iterator<Intersection>, var current: Intersection) : Comparable<Candidate> {
        fun next(): Boolean {
            return if (iterator.hasNext()) {
                current = iterator.next()
                true
            } else {
                false
            }
        }

        override fun compareTo(other: Candidate): Int {
            return current.t.compareTo(other.current.t)
        }
    }

    val candidates = sortedSetOf<Candidate>()

    multiple.forEach { intersections ->
        val iterator = intersections.iterator()
        if (iterator.hasNext()) {
            candidates.add(Candidate(iterator, iterator.next()))
        }
    }

    return Intersections(
        buildList {
            while (candidates.isNotEmpty()) {
                val nextCandidate = candidates.first()
                candidates.remove(nextCandidate)
                add(nextCandidate.current)
                if (nextCandidate.next()) {
                    candidates.add(nextCandidate)
                }
            }
        }
    )
}
