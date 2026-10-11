package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaJavaFieldSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaLocalVariableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty

private const val OTHER_FILE_DEPTH_PENALTY = 3

internal class ReferenceStringParts(private val evaluator: StringPartEvaluator) {

    context(session: KaSession)
    fun evaluate(
        expression: KtExpression,
        depth: Int,
        maxParts: Int,
    ): List<PartHolder> {
        val symbol = expression.resolvedSymbol()
        val property = symbol?.psi as? KtProperty
        val isOtherFile = property?.containingFile != expression.containingFile
        val propertyDepth = if (isOtherFile) depth - OTHER_FILE_DEPTH_PENALTY else depth
        return when (symbol) {
            is KaPropertySymbol if property != null && !property.isLocal && !property.isVar ->
                evaluator.evaluate(property.initializer, propertyDepth, maxParts)

            is KaLocalVariableSymbol if property != null && !property.isReassigned() ->
                evaluator.evaluate(property.initializer, depth, maxParts)

            is KaJavaFieldSymbol -> listOf(expression.constantText())

            else -> listOf(PartHolder.UNKNOWN)
        }
    }

    context(session: KaSession)
    private fun KtExpression.constantText(): PartHolder {
        val text = with(session) { evaluate() }?.value as? String
        return text?.let { PartHolder(it, isConstant = true) } ?: PartHolder.UNKNOWN
    }
}
