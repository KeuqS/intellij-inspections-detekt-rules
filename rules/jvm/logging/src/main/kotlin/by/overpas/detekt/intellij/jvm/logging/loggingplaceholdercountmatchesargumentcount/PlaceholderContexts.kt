package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import by.overpas.detekt.intellij.jvm.logging.BuilderChain
import by.overpas.detekt.intellij.jvm.logging.LogMethod
import by.overpas.detekt.intellij.jvm.logging.SLF4J_TO_LOG4J_BRIDGE
import by.overpas.detekt.intellij.jvm.logging.logMethod
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.placeholderContext(isSlf4jOnLog4j: Boolean?): PlaceholderContext? {
    val method = logMethod()
    val parameters = with(session) { resolveToCall()?.successfulFunctionCallOrNull() }?.run { symbol.valueParameters }
    val hasSlf4jBridge = isSlf4jOnLog4j ?: (with(session) { findClass(SLF4J_TO_LOG4J_BRIDGE) } != null)
    val loggerType = method?.let { placeholderLoggerType(it, hasSlf4jBridge) }
    if (method == null || parameters == null || loggerType == null) return null
    val sources = PlaceholderContextSources(this, loggerType, BuilderChain(method == LogMethod.SLF4J_BUILDER))
    val isBuilderMessage = parameters.isEmpty() || valueArguments.isEmpty()
    return if (isBuilderMessage) sources.ofBuilder() else sources.ofArguments(parameters)
}
