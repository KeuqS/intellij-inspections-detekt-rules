package by.overpas.detekt.intellij.jvm.declarationredundancy

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.components.directlyOverriddenSymbols
import org.jetbrains.kotlin.analysis.api.components.isNothingType
import org.jetbrains.kotlin.analysis.api.components.isUnitType
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

context(session: KaSession)
internal fun KtNamedFunction.sameReturnValue(): String? {
    if (!hasFixedReturnValues()) return null
    val explicit = collectDescendantsOfType<KtReturnExpression> { it.returnsFrom(this) }
        .mapNotNull { it.returnedExpression }
    val implicit = listOfNotNull(bodyExpression.takeUnless { hasBlockBody() })
    val values = (explicit + implicit).flatMap { it.returnedLeaves() }
    val templates = values.map { it.returnValueTemplate() }.distinct()
    return templates.singleOrNull()?.takeIf { values.size > 1 }
}

context(session: KaSession)
private fun KtNamedFunction.hasFixedReturnValues(): Boolean =
    with(session) {
        val owner = symbol.containingSymbol as? KaClassSymbol
        val isOverridable = symbol.modality != KaSymbolModality.FINAL &&
            symbol.visibility != KaSymbolVisibility.PRIVATE &&
            owner != null &&
            owner.modality != KaSymbolModality.FINAL
        val returnType = symbol.returnType
        !isOverridable &&
            !returnType.isUnitType &&
            !returnType.isNothingType &&
            !symbol.directlyOverriddenSymbols.any()
    }
