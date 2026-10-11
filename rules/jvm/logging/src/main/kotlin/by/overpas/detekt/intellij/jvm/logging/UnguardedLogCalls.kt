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
        call.logMethod()?.kind == LogMethodKind.LOGGER &&
            !limitLevelType.skips(call.loggerLevel()) &&
            !call.isSurroundedByLogGuard() &&
            !arguments.areOnlyConstant(call) &&
            !continuesSimilarCall(call) &&
            !call.qualifiedOrThis.isImplicitlyReturned()

    context(session: KaSession)
    private fun continuesSimilarCall(call: KtCallExpression): Boolean {
        val previous = PsiTreeUtil.skipWhitespacesAndCommentsBackward(call.qualifiedOrThis) as? KtQualifiedExpression
        val previousCall = previous?.selectorExpression as? KtCallExpression
        val receiverText = call.callReceiver?.text
        return previousCall != null &&
            receiverText != null &&
            previousCall.logMethod()?.kind == LogMethodKind.LOGGER &&
            previousCall.callReceiver?.text == receiverText &&
            !arguments.areOnlyConstant(previousCall) &&
            previousCall.calleeName == call.calleeName &&
            previousCall.loggerLevel() == call.loggerLevel()
    }
}
