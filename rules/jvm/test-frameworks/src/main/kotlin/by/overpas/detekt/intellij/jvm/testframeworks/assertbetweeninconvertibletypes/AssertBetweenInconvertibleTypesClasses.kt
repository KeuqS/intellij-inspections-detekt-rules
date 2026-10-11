package by.overpas.detekt.intellij.jvm.testframeworks.assertbetweeninconvertibletypes

import by.overpas.detekt.intellij.jvm.testframeworks.superClassIds
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality

private val FINAL_CLASS_KINDS = setOf(KaClassKind.OBJECT, KaClassKind.COMPANION_OBJECT, KaClassKind.ANONYMOUS_OBJECT)
private val INTERFACE_CLASS_KINDS = setOf(KaClassKind.INTERFACE, KaClassKind.ANNOTATION_CLASS)

context(session: KaSession)
internal fun KaClassSymbol.isConvertibleTo(other: KaClassSymbol): Boolean {
    val isRelated = this == other ||
        (classId != null && classId == other.classId) ||
        other.classId in superClassIds() ||
        classId in other.superClassIds()
    val hasPossibleCommonSubclass = !isFinal() &&
        !other.isFinal() &&
        (classKind in INTERFACE_CLASS_KINDS || other.classKind in INTERFACE_CLASS_KINDS)
    return isRelated || hasPossibleCommonSubclass
}

private fun KaClassSymbol.isFinal(): Boolean =
    modality == KaSymbolModality.FINAL || classKind in FINAL_CLASS_KINDS
