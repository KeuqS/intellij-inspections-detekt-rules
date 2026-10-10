package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol

context(session: KaSession)
internal fun KaClassSymbol.parentWithRunWith(): KaNamedClassSymbol? {
    val visited = mutableSetOf<KaClassSymbol>()
    val queue = ArrayDeque(superClassSymbols())
    while (queue.isNotEmpty()) {
        val parent = queue.removeFirst()
        if (!visited.add(parent)) continue
        if (parent is KaNamedClassSymbol && parent.hasAnnotation(ORG_JUNIT_RUN_WITH)) return parent
        queue.addAll(parent.superClassSymbols())
    }
    return null
}
