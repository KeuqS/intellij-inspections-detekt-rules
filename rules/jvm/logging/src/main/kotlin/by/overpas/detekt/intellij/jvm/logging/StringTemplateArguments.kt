package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression

internal class StringTemplateArguments(
    private val limitLevelType: LimitLevelType,
    private val arePrimitivesSkipped: Boolean,
    private val isOnlyExceptionSkipped: Boolean,
) {

    context(session: KaSession)
    fun isReported(call: KtCallExpression): Boolean {
        val kind = call.logMethod()?.kind
        val isReportedKind = kind == LogMethodKind.LOGGER || kind == LogMethodKind.BUILDER
        if (!isReportedKind || limitLevelType.skips(call.loggerLevel())) return false
        val arguments = call.valueArguments.map { it.getArgumentExpression() }
        val index = call.messageIndex(arguments.size)
        val parts = index?.let { arguments[it] }?.templateParts().orEmpty()
        val isOnlyException = index != null &&
            isOnlyExceptionSkipped &&
            arguments.lastIndex - index == 1 &&
            arguments.last().isThrowable()
        val isThrowableInjected = parts.lastOrNull { !it.isLiteral }?.expression.isThrowable()
        val arePrimitivesOnly = arePrimitivesSkipped && parts.all { it.isPrimitiveOrText() }
        val isSkipped = isOnlyException || isThrowableInjected || arePrimitivesOnly
        return parts.isNotEmpty() && !isSkipped && !call.isGuardedAtSameLevel()
    }
}
