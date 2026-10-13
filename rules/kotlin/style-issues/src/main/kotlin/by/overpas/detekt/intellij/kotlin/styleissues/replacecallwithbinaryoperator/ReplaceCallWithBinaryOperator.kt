package by.overpas.detekt.intellij.kotlin.styleissues.replacecallwithbinaryoperator

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtSuperExpression

private val replaceCallWithBinaryOperatorNames = setOf("equals", "compareTo")

@IntellijInspection("ReplaceCallWithBinaryOperator")
class ReplaceCallWithBinaryOperator(config: Config) :
    Rule(
        config,
        "An explicit call of `equals` or `compareTo` is harder to read " +
            "than the operator. Use the binary operator instead.",
    ),
    RequiresAnalysisApi {

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        val call = expression.selectorExpression as? KtCallExpression
        val callee = call?.calleeExpression as? KtSimpleNameExpression ?: return
        val isCandidate = expression.receiverExpression !is KtSuperExpression &&
            call.valueArguments.size == 1 &&
            callee.getReferencedName() in replaceCallWithBinaryOperatorNames
        if (isCandidate && expression.isBinaryOperatorReplaceable()) {
            report(Finding(Entity.from(callee), "Call can be replaced with binary operator"))
        }
    }
}
