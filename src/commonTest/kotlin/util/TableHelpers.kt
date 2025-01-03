package garden.ephemeral.rocket.util

import garden.ephemeral.rocket.util.RealParser.Companion.realFromString
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.data.Row2
import io.kotest.data.Row3
import io.kotest.data.Table2
import io.kotest.data.Table3
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.datatest.withData

internal inline fun <reified T> mapCell(string: String) = when (T::class) {
    String::class -> string as T
    Double::class -> when (string) {
        "Infinity" -> Double.POSITIVE_INFINITY
        "-Infinity" -> Double.NEGATIVE_INFINITY
        "NaN" -> Double.NaN
        else -> realFromString(string)
    } as T
    Complex::class -> Complex(string) as T
    else -> throw UnsupportedOperationException("Add support for converting type: ${T::class}")
}

private val separator = "|"
private val separatorRegex = Regex("""([\\]{2}|[^\\]|^)\|""")

private fun parseRow(line: String): List<String> {
    require(line.startsWith(separator) && line.endsWith(separator)) { "Unrecognised line format: $line" }
    val trimmed = line.replace(" ", "")
    return line
        .removePrefix(separator)
        .removeSuffix(separator)
        .trim()
        .split(separatorRegex)
        .map {
            val cell = it.trim()
            val suffix = if ("$cell\\\\|" in trimmed) "\\" else ""
            cell.plus(suffix)
                .replace("\\|", "|")
                .replace("\\\\", "\\")
        }
}

private fun parseRows(string: String): List<List<String>> {
    val lines = string.lines()
        .map(String::trim)
        .filterNot { line -> line.startsWith('#') }
        .map(::parseRow)

    require(lines.isNotEmpty()) { "Table must have a header" }

    return lines
}

internal inline fun <reified A, reified B> stringTable2(string: String): Table2<A, B> {
    val lines = parseRows(string)
    val headers = lines.take(1)
        .map { (a, b) -> headers(a, b) }
        .first()
    val rows = lines.drop(1)
        .map { (a, b) -> row(mapCell<A>(a), mapCell<B>(b)) }
    return table(headers = headers, rows = rows)
}

internal inline fun <reified A, reified B, reified C> stringTable3(string: String): Table3<A, B, C> {
    val lines = parseRows(string)
    val headers = lines.take(1)
        .map { (a, b, c) -> headers(a, b, c) }
        .first()
    val rows = lines.drop(1)
        .map { (a, b, c) -> row(mapCell<A>(a), mapCell<B>(b), mapCell<C>(c)) }
    return table(headers = headers, rows = rows)
}

internal suspend inline fun <reified A, reified B> ContainerScope.withStringTable2(
    string: String,
    noinline test: suspend ContainerScope.(Row2<A, B>) -> Unit
) {
    val table = stringTable2<A, B>(string)
    withData(table.rows, test)
}

internal suspend inline fun <reified A, reified B, reified C> ContainerScope.withStringTable3(
    string: String,
    noinline test: suspend ContainerScope.(Row3<A, B, C>) -> Unit
) {
    val table = stringTable3<A, B, C>(string)
    withData(table.rows, test)
}
