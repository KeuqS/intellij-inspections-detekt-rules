package by.overpas.detekt.intellij.jvm.logging

internal class PartIterator(private val parts: List<PartHolder>) {

    private var index = 0

    private var partial: String? = null

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

    fun move() {
        index++
        partial = null
    }

    fun move(delta: Int) {
        partial = current.substring(delta)
    }
}
