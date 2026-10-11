package by.overpas.detekt.intellij.jvm.logging

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtIfExpression

internal fun Pair<KtCallExpression, KtCallExpression>.isInSequence(): Boolean {
    val common = PsiTreeUtil.findCommonParent(first, second)
    if (common !is KtBlockExpression && common !is KtDeclarationWithBody) return false
    return first.isDirectlyIn(common) || second.isDirectlyIn(common)
}

private fun KtCallExpression.isDirectlyIn(common: PsiElement): Boolean {
    val parent = qualifiedOrThis.logicalParent
    val grandParent = parent?.logicalParent
    return parent == common ||
        (grandParent is KtIfExpression && grandParent.logicalParent == common) ||
        (parent is KtIfExpression && grandParent == common)
}
