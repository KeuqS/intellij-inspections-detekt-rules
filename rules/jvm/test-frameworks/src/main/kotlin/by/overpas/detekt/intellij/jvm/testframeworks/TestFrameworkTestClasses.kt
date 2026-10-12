package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol

context(session: KaSession)
internal fun KaClassSymbol.isTestClass(): Boolean =
    JUNIT_FRAMEWORK_TEST_CASE in superClassIds() ||
        hasAnnotation(TESTNG_TEST) ||
        declaredFunctions().any { it.isTestFunction(this) }

context(session: KaSession)
internal fun KaClassSymbol.isTestFrameworkClass(): Boolean =
    isTestClass() ||
        hasAnnotation(JUPITER_NESTED) ||
        declaredInnerClasses().any { it.hasAnnotation(JUPITER_NESTED) } ||
        declaredFunctions().any { function ->
            function.annotations.any { annotation -> annotation.classId in LIFECYCLE_ANNOTATIONS }
        }
