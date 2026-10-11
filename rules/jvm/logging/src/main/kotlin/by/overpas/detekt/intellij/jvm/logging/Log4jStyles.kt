package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

internal object Log4jStyles {

    context(session: KaSession)
    fun loggerStyle(call: KtCallExpression): PlaceholderLoggerType? {
        val initializers = when (val qualifier = call.immediateQualifier) {
            is KtNameReferenceExpression ->
                (qualifier.resolvedSymbol() as? KaVariableSymbol)
                    ?.let(::LoggerVariable)
                    ?.takeIf { it.isFinal }
                    ?.initializers()

            is KtCallExpression -> listOf(qualifier)

            else -> null
        }
        return initializers.orEmpty()
            .asSequence()
            .map { initializer ->
                if (initializer.selectorOrThis.isFormatterLoggerCall()) {
                    PlaceholderLoggerType.LOG4J_FORMATTED_STYLE
                } else {
                    PlaceholderLoggerType.LOG4J_OLD_STYLE
                }
            }
            .distinct()
            .singleOrNull()
    }
}
