package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds

private val EQUALS = Name.identifier("equals")

private val IDENTITY_CLASS_KINDS = setOf(KaClassKind.CLASS, KaClassKind.OBJECT)

context(session: KaSession)
internal fun KaNamedClassSymbol.hasIdentityEquals(): Boolean {
    val owners = with(session) { memberScope.callables(EQUALS).mapNotNull { it.callableId?.classId }.toList() }
    return modality == KaSymbolModality.FINAL &&
        classKind in IDENTITY_CLASS_KINDS &&
        owners.isNotEmpty() &&
        owners.all { it == StandardClassIds.Any || it == JAVA_LANG_OBJECT }
}
