package garden.ephemeral.rocket

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll

suspend fun <A> Iterable<A>.parallelForEach(f: suspend (A) -> Unit): Unit = coroutineScope {
    map { job -> async(Default) { f(job) } }.joinAll()
}