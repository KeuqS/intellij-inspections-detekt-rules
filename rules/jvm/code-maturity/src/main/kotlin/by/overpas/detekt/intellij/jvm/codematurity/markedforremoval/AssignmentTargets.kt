package by.overpas.detekt.intellij.jvm.codematurity.markedforremoval

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

internal fun KtNameReferenceExpression.isAssigned(): Boolean {
    val reference = (parent as? KtQualifiedExpression)?.takeIf { it.selectorExpression == this } ?: this
    val assignment = reference.parent as? KtBinaryExpression
    return assignment?.operationToken == KtTokens.EQ && assignment.left == reference
}
