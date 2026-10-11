package by.overpas.detekt.intellij.jvm.logging

private const val PREFILTER_SIZE = 5

internal fun List<MessageLog>.prefiltered(): Collection<List<MessageLog>> =
    when {
        size <= 1 -> emptyList()
        size <= PREFILTER_SIZE -> listOf(this)
        else -> groupedByCommonText()
    }

private fun List<MessageLog>.groupedByCommonText(): Collection<List<MessageLog>> {
    val partsList = mapNotNull { it.parts }
    val firstLength = partsList
        .filter { it.firstOrNull()?.isNotBlankText == true }
        .minOfOrNull { it.first().text.orEmpty().length }
    val isFirstTaken = firstLength != null && firstLength != 0
    val minLength = if (isFirstTaken) {
        firstLength
    } else {
        partsList.filter { it.lastOrNull()?.isNotBlankText == true }.minOfOrNull { it.last().text.orEmpty().length }
    }
    return if (minLength == null || minLength == 0) {
        emptyList()
    } else {
        groupBy { log ->
            val parts = log.parts.orEmpty()
            val part = if (isFirstTaken) parts.firstOrNull() else parts.lastOrNull()
            val text = part?.text?.takeIf { part.isConstant && it.length >= minLength }
            text?.substring(0, minLength).orEmpty()
        }.values
    }
}
