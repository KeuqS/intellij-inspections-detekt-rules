package by.overpas.detekt.intellij.jvm.logging.loggingguardedbycondition

import by.overpas.detekt.intellij.jvm.logging.qualifiedOrThis
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

internal fun KtIfExpression.isUnwrappable(
    guard: KtExpression,
    guardedCall: KtCallExpression,
    calls: List<KtCallExpression>,
): Boolean {
    val thenBranch = then
    val statements = (thenBranch as? KtBlockExpression)?.statements
    val isSingleCall = thenBranch == guardedCall || thenBranch == guardedCall.qualifiedOrThis
    val areAllCalls = statements != null &&
        statements.size == calls.size &&
        statements.zip(calls).all { (statement, call) -> statement == call || statement == call.qualifiedOrThis }
    return condition == guard.qualifiedOrThis && (isSingleCall || areAllCalls)
}
