package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

internal class PlaceholderText(
    private val text: String,
    private val isBackslashEscaping: Boolean,
) {

    fun count(isAfterUnknown: Boolean): Int =
        (1..<text.length).count { index ->
            val isPlaceholder = text[index] == '}' && text[index - 1] == '{'
            val isSkippedStart = isAfterUnknown && index == 1
            val backslashes = text.take(index - 1).takeLastWhile { it == '\\' }.length
            isPlaceholder && !isSkippedStart && !(isBackslashEscaping && backslashes == 1)
        }
}
