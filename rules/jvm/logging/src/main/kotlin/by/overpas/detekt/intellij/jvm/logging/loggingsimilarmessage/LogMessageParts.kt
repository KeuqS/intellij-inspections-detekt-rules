package by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage

import by.overpas.detekt.intellij.jvm.logging.BuilderChain
import by.overpas.detekt.intellij.jvm.logging.LogMethod
import by.overpas.detekt.intellij.jvm.logging.PartHolder
import by.overpas.detekt.intellij.jvm.logging.logStringIndex
import by.overpas.detekt.intellij.jvm.logging.stringParts
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
