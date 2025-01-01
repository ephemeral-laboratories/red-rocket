package garden.ephemeral.rocket.util

import kotlin.reflect.KProperty

class ToStringBuilder(self: Any) {
    private val stringBuilder = StringBuilder()
        .append(self.javaClass.simpleName, '@', System.identityHashCode(self), '(')

    private var first = true

    fun add(property: KProperty<*>): ToStringBuilder {
        if (first) {
            first = false
        } else {
            stringBuilder.append(", ")
        }
        stringBuilder.append(property.name, '=', property.call())
        return this
    }

    override fun toString(): String = stringBuilder.append(')').toString()
}
