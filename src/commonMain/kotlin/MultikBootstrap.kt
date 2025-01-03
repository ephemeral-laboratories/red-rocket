package garden.ephemeral.rocket

import org.jetbrains.kotlinx.multik.api.DefaultEngineType
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.NativeEngineType
import org.jetbrains.kotlinx.multik.api.enginesProvider
import org.jetbrains.kotlinx.multik.api.mk

fun bootstrapMultik() {
    mk.setEngine(DefaultEngineType)
    mk.setEngine(NativeEngineType)
    mk.setEngine(KEEngineType)
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

fun benchmarkMultik(action: () -> Unit) {
    sequenceOf(DefaultEngineType, NativeEngineType, KEEngineType).forEach { engineType ->
        println("Setting engine type to: ${engineType.name}")
        mk.setEngine(engineType)
        repeat(5) {
            action()
        }
        println()
    }
}
