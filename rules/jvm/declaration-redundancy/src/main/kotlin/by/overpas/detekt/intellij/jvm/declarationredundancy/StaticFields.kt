package by.overpas.detekt.intellij.jvm.declarationredundancy

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaEnumEntrySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaJavaFieldSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

context(session: KaSession)
internal fun KtExpression.staticField(): KaVariableSymbol? {
    val reference = (if (this is KtDotQualifiedExpression) selectorExpression else this) as? KtNameReferenceExpression
    return when (val symbol = reference?.let { with(session) { it.mainReference.resolveToSymbol() } }) {
        is KaEnumEntrySymbol -> symbol
        is KaJavaFieldSymbol -> symbol.takeIf { it.isStatic && it.isVal }
        is KaPropertySymbol -> symbol.takeIf { it.isVal && it.hasBackingField && it.isStatic() }
        else -> null
    }
}

context(session: KaSession)
private fun KaPropertySymbol.isStatic(): Boolean {
    val owner = with(session) { containingSymbol } as? KaClassSymbol
    return callableId?.classId == null ||
        owner?.classKind == KaClassKind.OBJECT ||
        owner?.classKind == KaClassKind.COMPANION_OBJECT
}
