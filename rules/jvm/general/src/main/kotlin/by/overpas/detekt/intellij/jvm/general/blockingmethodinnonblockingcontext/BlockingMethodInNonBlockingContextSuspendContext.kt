package by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents

internal class BlockingMethodInNonBlockingContextSuspendContext(private val expression: KtExpression) {

    context(session: KaSession)
    fun find(): KtFunction? =
        with(session) {
            val owner = expression.parents
                .filterIsInstance<KtFunction>()
                .firstOrNull { !BlockingMethodInNonBlockingContextLambda(it).isInlined() }
            when {
                owner == null -> null

                owner is KtFunctionLiteral || (owner is KtNamedFunction && owner.isAnonymous) ->
                    owner.takeIf {
                        BlockingMethodInNonBlockingContextLambda(owner).expression().expressionType
                            ?.isSuspendFunctionType == true
                    }

                else -> owner.takeIf { it.hasModifier(KtTokens.SUSPEND_KEYWORD) }
            }
        }
}
