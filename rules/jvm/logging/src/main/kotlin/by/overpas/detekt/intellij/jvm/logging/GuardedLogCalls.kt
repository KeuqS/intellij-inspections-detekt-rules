package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

context(session: KaSession)
internal fun KtExpression.guardedLogCalls(): List<KtCallExpression> {
    val source = callReceiver?.resolvedSymbol()
    val ifExpression = getStrictParentOfType<KtIfExpression>()
    if (source == null || ifExpression == null) return emptyList()
    return ifExpression
        .collectDescendantsOfType<KtQualifiedExpression> { it.receiverExpression.resolvedSymbol() == source }
        .mapNotNull { it.selectorExpression as? KtCallExpression }
        .filter { call -> call.logMethod()?.kind.let { it != null && it != LogMethodKind.OTHER } }
}
