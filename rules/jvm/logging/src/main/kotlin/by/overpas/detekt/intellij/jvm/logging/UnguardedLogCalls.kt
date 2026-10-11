package by.overpas.detekt.intellij.jvm.logging

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

internal class UnguardedLogCalls(
    private val limitLevelType: LimitLevelType,
    private val arguments: LogCallArguments,
) {

    context(session: KaSession)
    fun isUnguarded(call: KtCallExpression): Boolean =
        with(call) {
            logMethod()?.kind == LogMethodKind.LOGGER &&
                !limitLevelType.skips(loggerLevel()) &&
                !isSurroundedByLogGuard() &&
                !arguments.areOnlyConstant(this) &&
                !continuesSimilarCall() &&
                !qualifiedOrThis.isImplicitlyReturned()
        }

    context(session: KaSession)
    private fun KtCallExpression.continuesSimilarCall(): Boolean {
        val previous = PsiTreeUtil.skipWhitespacesAndCommentsBackward(qualifiedOrThis) as? KtQualifiedExpression
        val previousCall = previous?.selectorExpression as? KtCallExpression
        val receiverText = callReceiver?.text
        return previousCall != null &&
            receiverText != null &&
            previousCall.logMethod()?.kind == LogMethodKind.LOGGER &&
            previousCall.callReceiver?.text == receiverText &&
            !arguments.areOnlyConstant(previousCall) &&
            previousCall.calleeName == calleeName &&
            previousCall.loggerLevel() == loggerLevel()
    }
}
