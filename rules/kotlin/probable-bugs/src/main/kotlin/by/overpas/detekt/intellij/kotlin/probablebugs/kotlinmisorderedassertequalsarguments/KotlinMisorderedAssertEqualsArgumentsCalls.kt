package by.overpas.detekt.intellij.kotlin.probablebugs.kotlinmisorderedassertequalsarguments

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.idea.references.KtReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiUtil

private val EXPECTED_LIKE_CONVERSION = Regex("^(to|from)[A-Z]")

context(session: KaSession)
internal fun KtCallExpression.isExpectedLikeAssertCall(
    receiver: KtExpression?,
    isActual: Boolean,
): Boolean {
    val symbol = with(session) { resolveToCall()?.successfulFunctionCallOrNull()?.symbol }
    val name = (symbol as? KaNamedFunctionSymbol)?.run { name.asString() }
    val hasExpectedArguments = valueArguments.all {
        it.getArgumentExpression()?.isExpectedLikeAssertArgument(isActual) == true
    }
    val isConversion = name != null &&
        EXPECTED_LIKE_CONVERSION.containsMatchIn(name) &&
        symbol.valueParameters.isEmpty() &&
        receiver?.isConstantAssertArgument() == true
    val isExpectedLike = symbol?.isExpectedLikeFactory() == true ||
        isConversion ||
        receiver?.isClassLikeAssertQualifier() == true
    return (isActual && name == "expected") || (hasExpectedArguments && isExpectedLike)
}

context(session: KaSession)
private fun KtExpression.isClassLikeAssertQualifier(): Boolean =
    when (val expression = KtPsiUtil.safeDeparenthesize(this)) {
        is KtNameReferenceExpression ->
            expression.references
                .filterIsInstance<KtReference>()
                .map { with(session) { it.resolveToSymbol() } }
                .any { it is KaClassSymbol && it.classKind != KaClassKind.OBJECT }

        is KtDotQualifiedExpression -> expression.selectorExpression?.isClassLikeAssertQualifier() == true

        else -> false
    }
