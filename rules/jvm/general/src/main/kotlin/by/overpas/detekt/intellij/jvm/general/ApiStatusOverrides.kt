package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallableDeclaration

context(session: KaSession)
internal fun KtCallableDeclaration.apiStatusOverrides(): List<ApiStatusUsage> {
    val symbol = (with(session) { symbol } as? KaCallableSymbol)?.takeIf { hasModifier(KtTokens.OVERRIDE_KEYWORD) }
    val anchor = apiStatusAnchor()
    val overridden = symbol?.let { with(session) { it.directlyOverriddenSymbols } }.orEmpty()
    return overridden.map { ApiStatusUsage(anchor, it.apiStatusTarget(), ApiStatusUsage.Kind.OVERRIDE) }.toList()
}
