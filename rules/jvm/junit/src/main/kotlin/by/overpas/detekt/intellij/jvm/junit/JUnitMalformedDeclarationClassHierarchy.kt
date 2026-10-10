package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol

context(session: KaSession)
internal fun KaClassSymbol.selfAndSuperClasses(): Set<KaClassSymbol> =
    collectHierarchy(linkedSetOf())

context(session: KaSession)
private fun KaClassSymbol.collectHierarchy(visited: MutableSet<KaClassSymbol>): Set<KaClassSymbol> =
    with(session) {
        if (visited.add(this@collectHierarchy)) {
            superTypes.mapNotNull { it.expandedSymbol }.forEach { it.collectHierarchy(visited) }
        }
        visited
    }
