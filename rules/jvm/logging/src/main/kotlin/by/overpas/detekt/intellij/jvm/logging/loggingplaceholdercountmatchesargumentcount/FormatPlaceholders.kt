package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import by.overpas.detekt.intellij.jvm.logging.PartHolder

internal object FormatPlaceholders {

    fun count(parts: List<PartHolder>): PlaceholderCount {
        val prefix = parts.takeWhile { it.isText }.joinToString("") { it.text.orEmpty() }
        val isFull = parts.all { it.isText }
        val count = prefix.takeIf { it.isNotEmpty() }?.let { FormatSpecifiers(it, isFull).count() }
        val status = when {
            prefix.isEmpty() -> PlaceholdersStatus.EMPTY
            count == null -> PlaceholdersStatus.ERROR_TO_PARSE_STRING
            isFull -> PlaceholdersStatus.EXACTLY
            else -> PlaceholdersStatus.PARTIAL
        }
        return PlaceholderCount(count ?: 0, status)
    }
}
