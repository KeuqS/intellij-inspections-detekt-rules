package by.overpas.detekt.intellij.jvm.testframeworks.testcasewithconstructor

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtQualifiedExpression

context(session: KaSession)
internal fun KtBinaryExpression.isAssignmentToVal(): Boolean {
    val target = left?.let { KtPsiUtil.safeDeparenthesize(it) }
    val reference = (target as? KtQualifiedExpression)?.selectorExpression ?: target
    val symbol = (reference as? KtNameReferenceExpression)?.let { with(session) { it.mainReference.resolveToSymbol() } }
    return operationToken == KtTokens.EQ && (symbol as? KaPropertySymbol)?.isVal == true
}
