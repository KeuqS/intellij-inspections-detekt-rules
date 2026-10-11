package by.overpas.detekt.intellij.jvm.logging

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContainerNode
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtQualifiedExpression

internal val KtExpression.qualifiedOrThis: KtExpression
    get() = (parent as? KtQualifiedExpression)?.takeIf { it.selectorExpression == this } ?: this

internal val KtExpression.callReceiver: KtExpression?
    get() = (parent as? KtQualifiedExpression)?.takeIf { it.selectorExpression == this }?.receiverExpression

internal val KtExpression.selectorOrThis: KtExpression?
    get() = if (this is KtQualifiedExpression) selectorExpression else this

internal val KtExpression.immediateQualifier: KtExpression?
    get() = callReceiver?.run { KtPsiUtil.safeDeparenthesize(this).selectorOrThis }

internal val KtCallExpression.calleeName: String?
    get() = calleeExpression?.text

internal val KtCallExpression.previousInChain: KtCallExpression?
    get() = callReceiver?.selectorOrThis as? KtCallExpression

internal val PsiElement.logicalParent: PsiElement?
    get() = parent?.let { if (it is KtContainerNode) it.parent else it }
