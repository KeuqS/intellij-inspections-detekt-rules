package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

internal class LogGuardSource(private val source: KaSymbol) {

    context(session: KaSession)
    fun isReferencedBy(condition: KtExpression): Boolean =
        condition.collectDescendantsOfType<KtQualifiedExpression> { it.receiverExpression.resolvedSymbol() == source }
            .isNotEmpty()

    context(session: KaSession)
    fun selectorIn(condition: KtExpression): KtExpression? {
        val pending = ArrayDeque(listOf(condition))
        while (pending.isNotEmpty()) {
            when (val expression = pending.removeFirst()) {
                is KtQualifiedExpression ->
                    if (expression.receiverExpression.resolvedSymbol() == source) return expression.selectorExpression

                is KtBinaryExpression ->
                    if (expression.operationToken == KtTokens.ANDAND) {
                        pending.addAll(0, listOfNotNull(expression.left, expression.right))
                    }
            }
        }
        return null
    }
}
