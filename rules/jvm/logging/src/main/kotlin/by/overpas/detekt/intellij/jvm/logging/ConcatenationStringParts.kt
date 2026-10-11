package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression

internal class ConcatenationStringParts(private val evaluator: StringPartEvaluator) {

    context(session: KaSession)
    fun evaluate(
        expression: KtBinaryExpression,
        depth: Int,
        maxParts: Int,
    ): List<PartHolder> {
        if (expression.operationToken != KtTokens.PLUS) return listOf(PartHolder.UNKNOWN)
        val operands = listOf(expression.left, expression.right)
        val parts = operands.fold(emptyList<PartHolder>()) { result, operand ->
            if (result.size >= maxParts) result else result + evaluator.evaluate(operand, depth, maxParts - 1)
        }
        return if (parts.size >= maxParts) parts + PartHolder.UNKNOWN else parts
    }
}
