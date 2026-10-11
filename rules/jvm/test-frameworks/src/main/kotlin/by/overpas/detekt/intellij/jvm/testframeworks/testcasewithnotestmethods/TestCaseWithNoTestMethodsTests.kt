package by.overpas.detekt.intellij.jvm.testframeworks.testcasewithnotestmethods

import by.overpas.detekt.intellij.jvm.testframeworks.JUNIT_FRAMEWORK_TEST_CASE
import by.overpas.detekt.intellij.jvm.testframeworks.declaredFunctions
import by.overpas.detekt.intellij.jvm.testframeworks.declaredInnerClasses
import by.overpas.detekt.intellij.jvm.testframeworks.isDisabledTestNgTest
import by.overpas.detekt.intellij.jvm.testframeworks.isTestFunction
import by.overpas.detekt.intellij.jvm.testframeworks.superClassIds
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol

private const val SUITE_FUNCTION_NAME = "suite"

context(session: KaSession)
internal fun KaClassSymbol.hasTests(checkSuite: Boolean): Boolean =
    declaredFunctions().any { it.isTestFunction(this) && !it.isDisabledTestNgTest() } ||
        (checkSuite && hasSuiteFunction()) ||
        declaredInnerClasses().any { it.hasTests(checkSuite = false) }

context(session: KaSession)
private fun KaClassSymbol.hasSuiteFunction(): Boolean {
    val companion = (this as? KaNamedClassSymbol)?.companionObject
    return JUNIT_FRAMEWORK_TEST_CASE in superClassIds() &&
        (companion?.declaredFunctions().orEmpty() + declaredFunctions())
            .any { it.name.asString() == SUITE_FUNCTION_NAME && it.valueParameters.isEmpty() }
}
