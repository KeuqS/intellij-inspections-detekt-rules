package by.overpas.detekt.intellij.jvm.logging

internal fun List<PartHolder>.hasSameCallArgumentsAs(other: List<PartHolder>): Boolean {
    val arguments = flatMap { it.callArguments.orEmpty() }.toSet()
    val otherArguments = other.flatMap { it.callArguments.orEmpty() }.toSet()
    val hasCalls = any { it.callArguments != null } || other.any { it.callArguments != null }
    return !hasCalls || arguments == otherArguments
}

internal fun List<PartHolder>.hasSameShapeAs(other: List<PartHolder>): Boolean {
    val isText = first().isNotBlankText
    val isOtherText = other.first().isNotBlankText
    val areDifferentTexts = size == 1 && other.size == 1 && isText && isOtherText && this[0].text != other[0].text
    val textCount = count { it.isText }
    val otherTextCount = other.count { it.isText }
    return !areDifferentTexts &&
        isText == isOtherText &&
        last().isNotBlankText == other.last().isNotBlankText &&
        (textCount == 0 || otherTextCount == 0 || textCount == otherTextCount)
}
