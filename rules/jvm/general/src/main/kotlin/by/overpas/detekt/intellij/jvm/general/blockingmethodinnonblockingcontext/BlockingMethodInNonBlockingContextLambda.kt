package by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

internal class BlockingMethodInNonBlockingContextLambda(private val function: KtFunction) {

    private val lambda: KtExpression = function.parent as? KtLambdaExpression ?: function

    fun expression(): KtExpression =
        lambda

    context(session: KaSession)
    fun isInlined(): Boolean =
        with(session) {
            val isLambda = function is KtFunctionLiteral || (function is KtNamedFunction && function.isAnonymous)
            val argument = lambda.parent as? KtValueArgument
            val call = argument?.getStrictParentOfType<KtCallElement>()?.resolveToCall()?.successfulFunctionCallOrNull()
            val parameter = call?.valueArgumentMapping?.get(argument.getArgumentExpression())?.symbol
            val type = parameter?.returnType
            isLambda &&
                call?.symbol.isInline() &&
                parameter?.isNoinline == false &&
                !parameter.isCrossinline &&
                type?.isMarkedNullable == false &&
                (type.isFunctionType || type.isSuspendFunctionType)
        }

    private fun KaFunctionSymbol?.isInline(): Boolean =
        (this as? KaNamedFunctionSymbol)?.isInline == true ||
            (this as? KaConstructorSymbol)?.containingClassId == StandardClassIds.Array
}
