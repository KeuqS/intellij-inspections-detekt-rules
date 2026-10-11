package by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage

internal fun Pair<PartCursor, PartCursor>.equalStep(): PartStep =
    PartStep(first.current.length, first.advance() to second.advance())

internal fun Pair<PartCursor, PartCursor>.prefixStep(): PartStep? {
    val firstLength = first.current.length
    val secondLength = second.current.length
    return when {
        first.current.startsWith(second.current) -> PartStep(secondLength, first.skip(secondLength) to second.advance())
        second.current.startsWith(first.current) -> PartStep(firstLength, first.advance() to second.skip(firstLength))
        else -> null
    }
}

internal fun Pair<PartCursor, PartCursor>.suffixStep(): PartStep? {
    val firstText = first.current
    val secondText = second.current
    val length = when {
        firstText.endsWith(secondText) -> secondText.length
        secondText.endsWith(firstText) -> firstText.length
        else -> null
    }
    return length?.let { PartStep(it, first.advance() to second.advance()) }
}

internal fun Pair<PartCursor, PartCursor>.containedStep(): PartStep? {
    val firstText = first.current
    val secondText = second.current
    val secondIndex = firstText.indexOf(secondText).takeIf { second.isPreviousUnknown && it != -1 }
    val firstIndex = secondText.indexOf(firstText).takeIf { secondIndex == null && first.isPreviousUnknown && it != -1 }
    val secondEnd = secondIndex?.plus(secondText.length)
    val firstEnd = firstIndex?.plus(firstText.length)
    return when {
        secondEnd != null -> PartStep(secondEnd, first.skip(secondEnd) to second.advance())
        firstEnd != null -> PartStep(firstEnd, first.advance() to second.skip(firstEnd))
        else -> null
    }
}
