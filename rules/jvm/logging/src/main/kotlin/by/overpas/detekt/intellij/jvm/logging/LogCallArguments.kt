package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

internal class LogCallArguments(private val isUnguardedConstantFlagged: Boolean) {

    context(session: KaSession)
    fun areOnlyConstant(call: KtCallExpression): Boolean {
        val arguments = call.valueArguments.mapNotNull { it.getArgumentExpression() }
        val areConstant = arguments.map(KtPsiUtil::safeDeparenthesize).all { argument ->
            val isEvaluated = with(session) { argument.evaluate() } != null
            argument is KtLambdaExpression || argument.isConstantOrPolyadic() || isEvaluated
        }
        return arguments.isEmpty() || (!isUnguardedConstantFlagged && areConstant)
    }

    private fun KtExpression.isConstantOrPolyadic(): Boolean =
        when (this) {
            is KtConstantExpression -> true
            is KtStringTemplateExpression -> !hasInterpolation()
            is KtBinaryExpression -> listOfNotNull(left, right).all { it.isConstantOrPolyadic() }
            else -> false
        }
}
