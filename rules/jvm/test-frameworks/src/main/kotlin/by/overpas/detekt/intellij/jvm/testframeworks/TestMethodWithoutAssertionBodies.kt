package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

context(session: KaSession, matcher: TestMethodWithoutAssertionMatcher)
internal fun KtNamedFunction.hasAssertion(): Boolean {
    val last = bodyBlockExpression?.run { statements.lastOrNull() } ?: bodyExpression
    val call = (last as? KtQualifiedExpression)?.selectorExpression ?: last
    val target = (call as? KtCallExpression)?.resolveFunction()?.psi as? KtNamedFunction
    return containsAssertion() || target?.containsAssertion() == true
}

context(session: KaSession, matcher: TestMethodWithoutAssertionMatcher)
private fun KtDeclarationWithBody.containsAssertion(): Boolean {
    val body = bodyExpression
    val elements = body?.let { listOf(it) + it.collectDescendantsOfType<KtElement>() }.orEmpty()
    return elements.any { it.isAssertionCall() }
}
