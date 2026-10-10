package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.name.ClassId

context(session: KaSession)
internal fun KaClassSymbol.declaredFunctions(): List<KaNamedFunctionSymbol> =
    with(session) { declaredMemberScope.callables.filterIsInstance<KaNamedFunctionSymbol>().toList() }

context(session: KaSession)
internal fun KaClassSymbol.declaredInnerClasses(): List<KaNamedClassSymbol> =
    with(session) {
        declaredMemberScope.classifiers.filterIsInstance<KaNamedClassSymbol>().filter { it.isInner }.toList()
    }

context(session: KaSession)
internal fun KaClassSymbol.superClassIds(): Set<ClassId> =
    with(session) { defaultType.allSupertypes.filterIsInstance<KaClassType>().map { it.classId }.toSet() }
