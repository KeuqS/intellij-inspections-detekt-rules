package by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage

import by.overpas.detekt.intellij.jvm.logging.PartHolder

private const val MAX_PART_COUNT = 10

internal class MessageSimilarity(private val minTextLength: Int) {

    fun isSimilar(messages: Pair<List<PartHolder>?, List<PartHolder>?>): Boolean {
        val (first, second) = messages
        if (first.isNullOrEmpty() || second.isNullOrEmpty()) return false
        val parts = first to second
        return parts.hasSameCallArguments() &&
            first.size < MAX_PART_COUNT &&
            second.size < MAX_PART_COUNT &&
            parts.hasSameShape() &&
            PartIntersection(PartCursor(first) to PartCursor(second)).length()?.let { it >= minTextLength } == true
    }
}
