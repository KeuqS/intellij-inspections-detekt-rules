package by.overpas.detekt.intellij.jvm.logging

internal class BracePlaceholders(private val isBackslashEscaping: Boolean) {

    fun count(parts: List<PartHolder>): PlaceholderCount {
        val count = parts.withIndex().sumOf { (index, part) ->
            val isAfterUnknown = index != 0 && !parts[index - 1].isConstant
            val text = part.text.takeIf { part.isConstant }
            text?.let { PlaceholderText(it, isBackslashEscaping).count(isAfterUnknown) } ?: 0
        }
        val isFull = parts.all { it.isConstant }
        return PlaceholderCount(count, if (isFull) PlaceholdersStatus.EXACTLY else PlaceholdersStatus.PARTIAL)
    }
}
