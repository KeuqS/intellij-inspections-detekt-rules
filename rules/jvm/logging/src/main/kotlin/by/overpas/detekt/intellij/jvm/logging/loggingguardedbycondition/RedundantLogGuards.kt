package by.overpas.detekt.intellij.jvm.logging.loggingguardedbycondition

import by.overpas.detekt.intellij.jvm.logging.LogMethodKind
import by.overpas.detekt.intellij.jvm.logging.callReceiver
import by.overpas.detekt.intellij.jvm.logging.conditionLevel
import by.overpas.detekt.intellij.jvm.logging.guardCondition
import by.overpas.detekt.intellij.jvm.logging.guardedLogCalls
import by.overpas.detekt.intellij.jvm.logging.logMethod
import by.overpas.detekt.intellij.jvm.logging.loggerLevel
import by.overpas.detekt.intellij.jvm.logging.resolvedSymbol
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaSyntheticJavaPropertySymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

context(session: KaSession)
internal fun KtIfExpression.redundantLogGuard(isReportedOnlyIfFixPossible: Boolean): KtExpression? {
    val guard = condition
        ?.run { conditionCalls().singleOrNull { it.guardCondition() == null } }
        ?.takeIf { it.getStrictParentOfType<KtIfExpression>() == this }
    val conditionLevel = guard?.conditionLevel()
    val calls = guard?.guardedLogCalls().orEmpty()
    val guardedCall = calls.firstOrNull { call ->
        conditionLevel != null &&
            call.loggerLevel() == conditionLevel &&
            guard.callReceiver?.text == call.callReceiver?.text &&
            call.logMethod()?.kind == LogMethodKind.LOGGER
    }
    if (guard == null || guardedCall == null) return null
    val isFixPossible = isUnwrappable(guard, guardedCall, calls)
    return guard.takeIf { isFixPossible || !isReportedOnlyIfFixPossible }
}

context(session: KaSession)
private fun KtExpression.conditionCalls(): List<KtExpression> =
    collectDescendantsOfType<KtExpression> { expression ->
        expression is KtCallExpression ||
            (expression is KtNameReferenceExpression && expression.resolvedSymbol() is KaSyntheticJavaPropertySymbol)
    }
