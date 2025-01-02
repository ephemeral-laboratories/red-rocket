package garden.ephemeral.rocket

import org.jetbrains.kotlinx.multik.api.enginesProvider
import org.jetbrains.kotlinx.multik.api.mk

fun bootstrapMultik() {
    mk.engine?.let { engine ->
        println("Multik engines available:")
        val providerMap = enginesProvider()
        mk.engines.forEach { (name, engineType) ->
            val selectedMarker = if (name == engine) "*" else " "
            val engineTypeName = engineType::class.simpleName
            val engineImpl = providerMap[engineType]
            val engineImplTypeName = if (engineImpl != null) engineImpl::class.qualifiedName else ""
            println("$selectedMarker $name ($engineTypeName - $engineImplTypeName)")
        }
    }
}
