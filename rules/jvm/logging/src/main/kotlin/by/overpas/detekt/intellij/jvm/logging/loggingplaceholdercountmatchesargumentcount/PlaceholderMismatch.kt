package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import org.jetbrains.kotlin.analysis.api.KaSession

private val EXCEPTION_EXCLUDED_TYPES = setOf(
    PlaceholderLoggerType.SLF4J,
    PlaceholderLoggerType.LOG4J_OLD_STYLE,
    PlaceholderLoggerType.LOG4J_FORMATTED_STYLE,
)

internal class PlaceholderMismatch(
    private val context: PlaceholderContext,
    placeholders: PlaceholderCount,
) {

    private val count = placeholders.count

    private val results = PlaceholderResults(context, placeholders)

    context(session: KaSession)
    fun message(): String? {
        val argumentCount = context.placeholderArguments.size
        val isExceptionExcluded = context.isLastArgumentException && context.loggerType in EXCEPTION_EXCLUDED_TYPES
        val finalCount = if (isExceptionExcluded) argumentCount - 1 else argumentCount
        val result = when (context.loggerType) {
            PlaceholderLoggerType.SLF4J -> results.slf4jResult(finalCount)

            PlaceholderLoggerType.LOG4J_OLD_STYLE, PlaceholderLoggerType.LOG4J_FORMATTED_STYLE ->
                results.log4jResult(argumentCount)

            PlaceholderLoggerType.SLF4J_EQUAL_PLACEHOLDERS,
            PlaceholderLoggerType.LOG4J_EQUAL_PLACEHOLDERS,
            PlaceholderLoggerType.AKKA_PLACEHOLDERS,
            -> results.equalResult(finalCount)
        }
        return when (result) {
            PlaceholderResult.SUCCESS -> null

            PlaceholderResult.PARTIAL_MISMATCH ->
                "Fewer arguments provided ($finalCount) than placeholders specified (at least $count)"

            PlaceholderResult.MISMATCH if finalCount > count ->
                "More arguments provided ($finalCount) than placeholders specified ($count)"

            PlaceholderResult.MISMATCH -> "Fewer arguments provided ($finalCount) than placeholders specified ($count)"
        }
    }
}
