package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

private val EQUALS = Name.identifier("equals")

context(session: KaSession)
internal fun KtCallExpression.isAssertEqualsOnIdentityObjects(): Boolean {
    val hint = resolvedFunctionCall()?.assertEqualsHint() ?: return false
    return hint.firstArgument.hasIdentityEquals() && hint.secondArgument.hasIdentityEquals()
}

context(session: KaSession)
private fun KtExpression?.hasIdentityEquals(): Boolean {
    val type = with(session) { this@hasIdentityEquals?.expressionType }
    val classSymbol = with(session) { type?.expandedSymbol } as? KaNamedClassSymbol ?: return false
    return classSymbol.isFinalClass() && classSymbol.inheritsObjectEquals()
}

private fun KaNamedClassSymbol.isFinalClass(): Boolean =
    modality == KaSymbolModality.FINAL && classKind in setOf(KaClassKind.CLASS, KaClassKind.OBJECT)

context(session: KaSession)
private fun KaClassSymbol.inheritsObjectEquals(): Boolean {
    val owners = with(session) { memberScope.callables(EQUALS).mapNotNull { it.callableId?.classId }.toList() }
    return owners.isNotEmpty() && owners.all { it == StandardClassIds.Any || it == JAVA_LANG_OBJECT }
}
