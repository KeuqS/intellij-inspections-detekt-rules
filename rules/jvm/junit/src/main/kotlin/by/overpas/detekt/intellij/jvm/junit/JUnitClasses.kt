package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClassOrObject

context(session: KaSession)
internal fun KtClassOrObject.isJUnit3TestCase(): Boolean =
    with(session) { classSymbol?.isJUnit3TestCase() == true }

context(session: KaSession)
internal fun KaClassSymbol.isJUnit3TestCase(): Boolean {
    val testCase = with(session) { findClass(JUNIT_FRAMEWORK_TEST_CASE) }
    return testCase != null && with(session) { isSubClassOf(testCase) }
}

context(session: KaSession)
internal fun KaClassSymbol.superClassSymbols(): List<KaClassSymbol> =
    with(session) { superTypes.mapNotNull { it.expandedSymbol } }

context(session: KaSession)
internal fun KaClassSymbol.hasAnnotation(classId: ClassId): Boolean =
    classId in annotations
