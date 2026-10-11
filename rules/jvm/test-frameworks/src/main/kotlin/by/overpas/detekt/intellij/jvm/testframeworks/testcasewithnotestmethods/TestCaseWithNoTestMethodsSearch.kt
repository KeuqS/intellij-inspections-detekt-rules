package by.overpas.detekt.intellij.jvm.testframeworks.testcasewithnotestmethods

import by.overpas.detekt.intellij.jvm.testframeworks.ORG_JUNIT_IGNORE
import by.overpas.detekt.intellij.jvm.testframeworks.ORG_JUNIT_RUN_WITH
import by.overpas.detekt.intellij.jvm.testframeworks.hasAnnotation
import by.overpas.detekt.intellij.jvm.testframeworks.isTestFrameworkClass
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
