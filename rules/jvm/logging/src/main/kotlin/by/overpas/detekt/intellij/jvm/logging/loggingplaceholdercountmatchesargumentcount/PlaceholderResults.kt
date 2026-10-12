package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import by.overpas.detekt.intellij.jvm.logging.STRUCTURED_ARGUMENT
import org.jetbrains.kotlin.analysis.api.KaSession

internal class PlaceholderResults(
    private val context: PlaceholderContext,
    placeholders: PlaceholderCount,
) {

    private val count = placeholders.count

    private val isPartial = placeholders.status == PlaceholdersStatus.PARTIAL

    context(session: KaSession)
    fun slf4jResult(finalCount: Int): PlaceholderResult =
        when {
            isPartial && count <= finalCount -> PlaceholderResult.SUCCESS
            isPartial -> PlaceholderResult.PARTIAL_MISMATCH
            count == finalCount -> PlaceholderResult.SUCCESS
            else -> structuredResult(finalCount)
        }

    context(session: KaSession)
    fun equalResult(finalCount: Int): PlaceholderResult =
        when {
            isPartial && count <= finalCount -> PlaceholderResult.SUCCESS

            isPartial && context.loggerType == PlaceholderLoggerType.SLF4J_EQUAL_PLACEHOLDERS ->
                structuredResult(finalCount)

            isPartial -> PlaceholderResult.PARTIAL_MISMATCH

            count == finalCount -> PlaceholderResult.SUCCESS

            else -> PlaceholderResult.MISMATCH
        }

    fun log4jResult(argumentCount: Int): PlaceholderResult {
        val isException = context.isLastArgumentException
        val isSupplier = context.isLastArgumentSupplier
        val fits: (Int) -> Boolean = { required -> if (isPartial) count <= required else count == required }
        val isSuccess = (fits(argumentCount) && (!isException || argumentCount > 1)) ||
            (isException && fits(argumentCount - 1)) ||
            (isSupplier && (fits(argumentCount) || (!isPartial && fits(argumentCount - 1))))
        return when {
            isSuccess -> PlaceholderResult.SUCCESS
            isPartial -> PlaceholderResult.PARTIAL_MISMATCH
            else -> PlaceholderResult.MISMATCH
        }
    }

    context(session: KaSession)
    private fun structuredResult(finalCount: Int): PlaceholderResult {
        val arguments = context.placeholderArguments
        val isStructured = count in 0..<finalCount &&
            arguments.size >= finalCount &&
            (count..<finalCount).all { index ->
                val type = with(session) { arguments[index].expressionType }
                type == null || with(session) { type.isSubtypeOf(STRUCTURED_ARGUMENT) }
            }
        return if (isStructured) PlaceholderResult.SUCCESS else PlaceholderResult.MISMATCH
    }
}
