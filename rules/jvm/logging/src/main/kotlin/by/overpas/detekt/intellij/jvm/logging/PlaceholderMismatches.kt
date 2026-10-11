package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession

private val SLF4J_TYPES = setOf(PlaceholderLoggerType.SLF4J, PlaceholderLoggerType.SLF4J_EQUAL_PLACEHOLDERS)

context(session: KaSession)
internal fun PlaceholderContext.mismatchMessage(): String? {
    val placeholders = if (loggerType == PlaceholderLoggerType.LOG4J_FORMATTED_STYLE) {
        FormatPlaceholders.count(parts)
    } else {
        BracePlaceholders(isBackslashEscaping = loggerType in SLF4J_TYPES).count(parts)
    }
    return when (placeholders.status) {
        PlaceholdersStatus.EMPTY -> null
        PlaceholdersStatus.ERROR_TO_PARSE_STRING -> "Illegal format string specifier"
        PlaceholdersStatus.EXACTLY, PlaceholdersStatus.PARTIAL -> PlaceholderMismatch(this, placeholders).message()
    }
}
