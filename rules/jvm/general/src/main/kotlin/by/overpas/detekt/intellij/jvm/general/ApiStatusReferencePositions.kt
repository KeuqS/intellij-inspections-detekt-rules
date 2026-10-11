package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType

internal fun KtNameReferenceExpression.apiStatusCallElement(): KtCallElement? {
    val call = parent as? KtCallExpression
    if (call != null) return call.takeIf { it.calleeExpression == this }
    val userType = (parent as? KtUserType)?.takeIf { it.referenceExpression == this }
    val typeReference = userType?.parent as? KtTypeReference
    val callee = typeReference?.parent as? KtConstructorCalleeExpression
    return callee?.parent as? KtSuperTypeCallEntry
}

internal fun KtNameReferenceExpression.apiStatusQualifier(): KtExpression? {
    val callableReference = parent as? KtCallableReferenceExpression
    if (callableReference != null) return callableReference.receiverExpression
    val selector = (parent as? KtCallExpression)?.takeIf { it.calleeExpression == this } ?: this
    val qualified = selector.parent as? KtQualifiedExpression
    return qualified?.takeIf { it.selectorExpression == selector }?.receiverExpression
}
