package by.overpas.detekt.intellij.jvm.declarationredundancy

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

context(session: KaSession)
internal fun KtNamedFunction.callArguments(): List<List<ArgumentValue>>? {
    val calls = containingKtFile
        .collectDescendantsOfType<KtSimpleNameExpression> { it.getReferencedName() == name }
        .filter { with(session) { it.mainReference.resolveToSymbol() }?.psi == this }
        .map { it.callArgumentsOf(this) }
    return calls.filterNotNull().takeIf { it.size == calls.size }
}

context(session: KaSession)
private fun KtSimpleNameExpression.callArgumentsOf(function: KtNamedFunction): List<ArgumentValue>? {
    val call = (parent as? KtCallExpression)?.takeIf { it.calleeExpression == this }
    val mapping = call?.takeIf { it.valueArguments.size >= function.valueParameters.size }
        ?.let { with(session) { it.resolveToCall() }?.singleFunctionCallOrNull()?.valueArgumentMapping }
        ?: return null
    return function.valueParameters.map { parameter ->
        val arguments = mapping.filterValues { it.symbol.psi == parameter }.keys
        val isSpread = arguments.any { (it.parent as? KtValueArgument)?.getSpreadElement() != null }
        when {
            isSpread -> ArgumentValue.NotConstant
            parameter.isVarArg -> varargValue(arguments.map { it.argumentValue() })
            else -> arguments.singleOrNull()?.argumentValue() ?: ArgumentValue.NotConstant
        }
    }
}
