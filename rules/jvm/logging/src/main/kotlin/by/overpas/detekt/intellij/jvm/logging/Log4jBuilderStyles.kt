package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

internal object Log4jBuilderStyles {

    context(session: KaSession)
    fun chainStyle(call: KtCallExpression): PlaceholderLoggerType? {
        val qualifier = call.immediateQualifier
        val variable = (qualifier as? KtNameReferenceExpression)
            ?.let { (it.resolvedSymbol() as? KaVariableSymbol)?.let(::LoggerVariable) }
        val styles = when {
            qualifier !is KtNameReferenceExpression -> listOf(qualifier).map { initializer -> initializer.style() }
            variable == null -> emptyList()
            !variable.isFinal -> listOf(PlaceholderLoggerType.LOG4J_EQUAL_PLACEHOLDERS)
            else -> variable.initializers().orEmpty().map { initializer -> initializer.style() }
        }
        return styles.distinct().singleOrNull()
    }

    context(session: KaSession)
    private fun KtExpression?.style(): PlaceholderLoggerType? {
        val selector = this?.selectorOrThis
        return if (selector !is KtCallExpression) {
            PlaceholderLoggerType.LOG4J_EQUAL_PLACEHOLDERS
        } else {
            when (Log4jStyles.loggerStyle(selector)) {
                PlaceholderLoggerType.LOG4J_FORMATTED_STYLE -> PlaceholderLoggerType.LOG4J_FORMATTED_STYLE

                PlaceholderLoggerType.LOG4J_OLD_STYLE -> PlaceholderLoggerType.LOG4J_EQUAL_PLACEHOLDERS

                PlaceholderLoggerType.SLF4J,
                PlaceholderLoggerType.SLF4J_EQUAL_PLACEHOLDERS,
                PlaceholderLoggerType.LOG4J_EQUAL_PLACEHOLDERS,
                PlaceholderLoggerType.AKKA_PLACEHOLDERS,
                null,
                -> null
            }
        }
    }
}
