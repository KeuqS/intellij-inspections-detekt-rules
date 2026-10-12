package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.psi.KtClassOrObject

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

context(session: KaSession)
internal fun KtClassOrObject.hasRunWithInHierarchy(): Boolean {
    val symbol = with(session) { classSymbol } ?: return false
    return ORG_JUNIT_RUN_WITH in symbol.annotations || symbol.parentWithRunWith() != null
}
