package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol

context(session: KaSession)
internal fun KaClassSymbol.hasTestsInSuperClasses(): Boolean {
    val visited = mutableSetOf<KaClassSymbol>()
    val hierarchy = generateSequence(this) { it.superClass() }.takeWhile { visited.add(it) }.toList()
    return hierarchy.drop(1).any { it.hasAnnotation(ORG_JUNIT_RUN_WITH) || it.hasTests(checkSuite = false) } ||
        hierarchy.any { it.hasUnresolvedSuperTypes() }
}
