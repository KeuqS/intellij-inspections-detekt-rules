package by.overpas.detekt.intellij.jvm.declarationredundancy

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

internal fun KtExpression.returnedLeaves(): List<KtExpression> =
    when (this) {
        is KtParenthesizedExpression -> expression?.returnedLeaves().orEmpty()
        is KtBlockExpression -> statements.lastOrNull()?.returnedLeaves().orEmpty()
        is KtIfExpression -> listOfNotNull(then, `else`).flatMap { it.returnedLeaves() }
        is KtWhenExpression -> entries.mapNotNull { it.expression?.branchValue() }.flatMap { it.returnedLeaves() }
        else -> listOf(this)
    }

private fun KtExpression.branchValue(): KtExpression? {
    val value = if (this is KtBlockExpression) statements.lastOrNull() else this
    return value?.takeUnless { it is KtReturnExpression }
}
