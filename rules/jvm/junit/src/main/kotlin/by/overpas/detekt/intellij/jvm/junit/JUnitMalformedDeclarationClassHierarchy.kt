package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol

context(session: KaSession)
internal fun KaClassSymbol.selfAndSuperClasses(): Set<KaClassSymbol> {
    val visited = mutableSetOf<KaClassSymbol>()
    return generateSequence(listOf(this)) { level ->
        with(session) { level.flatMap { symbol -> symbol.superTypes.mapNotNull { it.expandedSymbol } } }
            .filter(visited::add)
            .takeIf { it.isNotEmpty() }
    }.flatten().toSet()
}
