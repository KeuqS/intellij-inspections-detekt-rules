package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol

context(session: KaSession)
internal fun KaNamedFunctionSymbol.isTestFunction(owner: KaClassSymbol): Boolean =
    annotations.any { annotation -> annotation.classId?.isTestAnnotation() == true } ||
        (hasJUnit3TestSignature() && JUNIT_FRAMEWORK_TEST_CASE in owner.superClassIds()) ||
        (isTestNgClassLevelCandidate() && owner.hasAnnotation(TESTNG_TEST))
