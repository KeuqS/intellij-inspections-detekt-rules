package by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage

import by.overpas.detekt.intellij.jvm.logging.PartHolder

internal fun Pair<List<PartHolder>, List<PartHolder>>.hasSameCallArguments(): Boolean {
    val arguments = first.flatMap { it.callArguments.orEmpty() }.toSet()
    val otherArguments = second.flatMap { it.callArguments.orEmpty() }.toSet()
    val hasCalls = first.any { it.callArguments != null } || second.any { it.callArguments != null }
    return !hasCalls || arguments == otherArguments
}

internal fun Pair<List<PartHolder>, List<PartHolder>>.hasSameShape(): Boolean {
    val isText = first.first().isNotBlankText
    val isOtherText = second.first().isNotBlankText
    val areDifferentTexts =
        first.size == 1 && second.size == 1 && isText && isOtherText && first[0].text != second[0].text
    val textCount = first.count { it.isText }
    val otherTextCount = second.count { it.isText }
    return !areDifferentTexts &&
        isText == isOtherText &&
        first.last().isNotBlankText == second.last().isNotBlankText &&
        (textCount == 0 || otherTextCount == 0 || textCount == otherTextCount)
}
