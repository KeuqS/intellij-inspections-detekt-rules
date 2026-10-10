package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtNamedFunction

private val JUPITER_TEST_ANNOTATIONS = setOf(
    JUPITER_TEST,
    JUPITER_PARAMETERIZED_TEST,
    JUPITER_REPEATED_TEST,
    JUPITER_TEST_FACTORY,
    JUPITER_TEST_TEMPLATE,
)

context(session: KaSession)
internal fun KtNamedFunction.isJUnit5TestMethod(): Boolean =
    annotationEntries.any { it.resolvedClassId()?.isJUnit5TestAnnotation() == true }

context(session: KaSession)
internal fun KtNamedFunction.isJUnit4TestMethod(): Boolean =
    hasAnnotationEntry(ORG_JUNIT_TEST)

context(session: KaSession)
private fun ClassId.isJUnit5TestAnnotation(): Boolean =
    this in JUPITER_TEST_ANNOTATIONS ||
        with(session) { findClass(this@isJUnit5TestAnnotation) }?.hasAnnotation(JUNIT_PLATFORM_TESTABLE) == true
