package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.psi.KtClassOrObject

context(session: KaSession)
internal fun KtClassOrObject.isFinalApiStatusClass(): Boolean {
    val symbol = with(session) { symbol }
    val kind = (symbol as? KaClassSymbol)?.classKind
    return symbol.modality == KaSymbolModality.FINAL &&
        kind != KaClassKind.INTERFACE &&
        kind != KaClassKind.ANNOTATION_CLASS
}
