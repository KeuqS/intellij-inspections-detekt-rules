package by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage

import by.overpas.detekt.intellij.jvm.logging.PartHolder

internal class PartCursor(
    private val parts: List<PartHolder>,
    private val index: Int = 0,
    private val partial: String? = null,
) {

    val hasNext: Boolean
        get() = index < parts.size

    val isText: Boolean
        get() = parts[index].isText

    val isFirst: Boolean
        get() = index == 0

    val isLast: Boolean
        get() = index == parts.lastIndex

    val isPreviousUnknown: Boolean
        get() = index != 0 && !parts[index - 1].isText

    val current: String
        get() = partial ?: parts[index].text.orEmpty()

    fun advance(): PartCursor =
        PartCursor(parts, index + 1)

    fun skip(delta: Int): PartCursor =
        PartCursor(parts, index, current.substring(delta))
}
