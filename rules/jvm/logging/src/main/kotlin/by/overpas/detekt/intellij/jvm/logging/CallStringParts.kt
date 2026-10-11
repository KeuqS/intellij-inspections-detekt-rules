package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression

internal class CallStringParts(private val evaluator: StringPartEvaluator) {

    context(session: KaSession)
    fun evaluate(
        expression: KtCallExpression,
        depth: Int,
        maxParts: Int,
    ): List<PartHolder> {
        val stringArguments = expression.valueArguments
            .asSequence()
            .flatMap { evaluator.evaluate(it.getArgumentExpression(), depth - 1, maxParts) }
            .filter { it.isConstant }
            .mapNotNull { it.text }
            .toList()
        return listOf(PartHolder(null, isConstant = false, callArguments = stringArguments.ifEmpty { null }))
    }
}
