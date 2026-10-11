package by.overpas.detekt.intellij.jvm.logging

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

context(session: KaSession)
internal fun KtExpression.guardCondition(): KtExpression? {
    val source = loggerSource()?.let(::LogGuardSource) ?: return null
    val ifExpression = generateSequence(getStrictParentOfType<KtIfExpression>()) { it.getStrictParentOfType() }
        .firstOrNull { it.condition?.let { condition -> source.isReferencedBy(condition) } == true }
    val condition = ifExpression?.condition?.let(KtPsiUtil::safeDeparenthesize)
    val negation = condition as? KtPrefixExpression
    val branch = if (negation != null) ifExpression.`else` else ifExpression?.then
    val guard = if (negation != null) negation.baseExpression else condition
    val isGuarded = branch != null &&
        PsiTreeUtil.isAncestor(branch, this, false) &&
        (negation == null || negation.operationToken == KtTokens.EXCL)
    return guard?.takeIf { isGuarded }?.let { source.selectorIn(it) }
}
