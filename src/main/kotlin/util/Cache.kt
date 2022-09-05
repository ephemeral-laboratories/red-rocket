package garden.ephemeral.rocket.util

/**
 * Cache utility.
 */
class Cache<K, V> {
    private val cache = linkedMapOf<K, V>()

    fun get(key: K, calculationLogic: (K) -> V): V {
        // Fast path
        val value = cache[key]
        if (value != null) return value

        synchronized(cache) {
            return cache.computeIfAbsent(key, calculationLogic)
        }
    }
}