package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClassOrObject

context(session: KaSession)
internal fun KtClassOrObject.isSubclassOf(classId: ClassId): Boolean =
    with(session) { classSymbol?.isSubclassOf(classId) == true }

context(session: KaSession)
internal fun KaClassSymbol.isSubclassOf(classId: ClassId): Boolean {
    val superClass = with(session) { findClass(classId) } ?: return false
    return with(session) { isSubClassOf(superClass) }
}

context(session: KaSession)
internal fun KaClassSymbol.superClassSymbols(): List<KaClassSymbol> =
    with(session) { superTypes.mapNotNull { it.expandedSymbol } }

context(session: KaSession)
internal fun KaClassSymbol.hasAnnotation(classId: ClassId): Boolean = classId in annotations
