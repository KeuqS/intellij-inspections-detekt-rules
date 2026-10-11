package by.overpas.detekt.intellij.jvm.codematurity

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.components.directlyOverriddenSymbols
import org.jetbrains.kotlin.analysis.api.components.fakeOverrideOriginal
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSyntheticJavaPropertySymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNameReferenceExpression.removalUsage(): RemovalUsage? {
    val symbol = with(session) { mainReference.resolveToSymbol() }
    val target = when (symbol) {
        is KaSyntheticJavaPropertySymbol -> if (isAssigned()) symbol.javaSetterSymbol else symbol.javaGetterSymbol
        is KaCallableSymbol -> with(session) { symbol.fakeOverrideOriginal }
        else -> symbol
    }
    return target?.removalTarget()?.let { RemovalUsage(it.presentableName(), DeprecatedApi(it).since, it.psi) }
}

context(session: KaSession)
internal fun KtNamedFunction.overriddenRemovalClasses(): List<String> =
    with(session) {
        symbol.directlyOverriddenSymbols
            .map { it.fakeOverrideOriginal }
            .filter { DeprecatedApi(it).isMarkedForRemoval }
            .mapNotNull { (it.containingSymbol as? KaClassSymbol)?.presentableName() }
            .toList()
    }

context(session: KaSession)
private fun KaSymbol.removalTarget(): KaSymbol? {
    val api = DeprecatedApi(this)
    return when {
        api.isMarkedForRemoval -> this
        api.isDeprecated -> null
        this is KaCallableSymbol && overridesNonDeprecated() -> null
        else -> (with(session) { containingSymbol } as? KaClassSymbol)?.removalTarget()
    }
}

context(session: KaSession)
private fun KaCallableSymbol.overridesNonDeprecated(): Boolean =
    with(session) {
        directlyOverriddenSymbols.any { overridden ->
            val owner = overridden.containingSymbol as? KaClassSymbol
            owner != null && !DeprecatedApi(owner).isDeprecated && !DeprecatedApi(overridden).isDeprecated
        }
    }
