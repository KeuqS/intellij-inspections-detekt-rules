package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.messageParts(method: LogMethod): List<PartHolder>? {
    val function = with(session) { resolveToCall()?.successfulFunctionCallOrNull() }?.symbol ?: return null
    val arguments = valueArguments.mapNotNull { it.getArgumentExpression() }
    val parameters = function.valueParameters
    val message = if (parameters.isEmpty() || arguments.isEmpty()) {
        BuilderChain(method == LogMethod.SLF4J_BUILDER).messageSetterArgument(this)
    } else {
        parameters.logStringIndex()?.let { arguments.getOrNull(it - 1) }
    }
    return message?.stringParts()
}
