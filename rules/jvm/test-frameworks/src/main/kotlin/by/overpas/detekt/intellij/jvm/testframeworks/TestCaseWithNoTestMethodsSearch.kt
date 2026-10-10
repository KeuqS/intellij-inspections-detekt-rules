package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtClass

context(session: KaSession)
internal fun KtClass.hasNoTests(isSupersIgnored: Boolean): Boolean {
    val symbol = with(session) { classSymbol } ?: return false
    val isSkipped = symbol.hasAnnotation(ORG_JUNIT_IGNORE) ||
        symbol.hasAnnotation(ORG_JUNIT_RUN_WITH) ||
        !symbol.isTestFrameworkClass() ||
        symbol.hasTests(checkSuite = true)
    return !isSkipped && !(isSupersIgnored && symbol.hasTestsInSuperClasses())
}
