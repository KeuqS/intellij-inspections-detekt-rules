package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

context(session: KaSession)
internal fun KtCallExpression.isAssertEqualsOnIdentity(): Boolean {
    val (first, second) = resolvedFunctionCall()?.assertEqualsHint() ?: return false
    return first.hasIdentityEquals() && second.hasIdentityEquals()
}

context(session: KaSession)
private fun KtExpression?.hasIdentityEquals(): Boolean {
    val type = with(session) { this@hasIdentityEquals?.expressionType }
    val classSymbol = with(session) { type?.expandedSymbol } as? KaNamedClassSymbol
    return classSymbol?.hasIdentityEquals() == true
}
