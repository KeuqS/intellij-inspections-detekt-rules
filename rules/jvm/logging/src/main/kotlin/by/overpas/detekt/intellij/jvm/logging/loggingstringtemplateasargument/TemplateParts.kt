package by.overpas.detekt.intellij.jvm.logging.loggingstringtemplateasargument

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

internal fun KtExpression.templateParts(): List<TemplatePart> =
    when (this) {
        is KtStringTemplateExpression if hasInterpolation() -> entryParts
        is KtBinaryExpression if operationToken == KtTokens.PLUS -> operandParts()
        else -> emptyList()
    }

private fun KtBinaryExpression.operandParts(): List<TemplatePart> {
    val operands = listOfNotNull(left, right)
    val isPattern = operands.none { it is KtConstantExpression } &&
        operands.any { it is KtStringTemplateExpression && it.hasInterpolation() }
    if (!isPattern) return emptyList()
    return operands.flatMap { operand ->
        when (operand) {
            is KtStringTemplateExpression if operand.hasInterpolation() -> operand.entryParts
            is KtStringTemplateExpression -> listOf(TemplatePart(operand, isLiteral = true))
            else -> listOf(TemplatePart(operand, isLiteral = false))
        }
    }
}
