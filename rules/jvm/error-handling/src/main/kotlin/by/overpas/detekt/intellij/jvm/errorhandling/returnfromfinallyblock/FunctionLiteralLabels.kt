package by.overpas.detekt.intellij.jvm.errorhandling.returnfromfinallyblock

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

internal fun KtFunctionLiteral.labels(): List<String> {
    val lambda = parent as? KtLambdaExpression ?: return emptyList()
    val labeled = lambda.parent as? KtLabeledExpression
    val argument = (labeled ?: lambda).parent as? KtValueArgument
    val call = (argument?.parent as? KtValueArgumentList)?.parent ?: argument?.parent
    return listOfNotNull(labeled?.getLabelName(), (call as? KtCallExpression)?.calleeExpression?.text)
}
