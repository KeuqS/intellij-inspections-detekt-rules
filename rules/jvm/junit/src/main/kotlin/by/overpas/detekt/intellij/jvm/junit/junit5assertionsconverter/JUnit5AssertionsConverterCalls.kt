package by.overpas.detekt.intellij.jvm.junit.junit5assertionsconverter

import by.overpas.detekt.intellij.jvm.junit.ASSERT_METHOD_PARAMETER_COUNT
import by.overpas.detekt.intellij.jvm.junit.JUNIT_FRAMEWORK_ASSERT
import by.overpas.detekt.intellij.jvm.junit.JUNIT_FRAMEWORK_TEST_CASE
import by.overpas.detekt.intellij.jvm.junit.JUPITER_ASSERTIONS
import by.overpas.detekt.intellij.jvm.junit.JUPITER_ASSUMPTIONS
import by.overpas.detekt.intellij.jvm.junit.JUPITER_TEST
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_ASSERT
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_ASSUME
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_TEST
import by.overpas.detekt.intellij.jvm.junit.TESTNG_ASSERT_JUNIT
import by.overpas.detekt.intellij.jvm.junit.assertHint
import by.overpas.detekt.intellij.jvm.junit.isJUnit5TestClass
import by.overpas.detekt.intellij.jvm.junit.isOnClasspath
import by.overpas.detekt.intellij.jvm.junit.resolvedFunctionCall
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

private val OBSOLETE_ASSERTION_OWNERS = setOf(
    TESTNG_ASSERT_JUNIT,
    JUNIT_FRAMEWORK_ASSERT,
    ORG_JUNIT_ASSERT,
    JUNIT_FRAMEWORK_TEST_CASE,
    ORG_JUNIT_ASSUME,
)

internal data class ObsoleteAssertion(val message: String)

context(session: KaSession)
internal fun KtFile.hasJUnit5TestClass(): Boolean =
    ORG_JUNIT_TEST.isOnClasspath() &&
        JUPITER_TEST.isOnClasspath() &&
        collectDescendantsOfType<KtClassOrObject>().any { it.isJUnit5TestClass() }

context(session: KaSession)
internal fun KtCallExpression.obsoleteAssertion(): ObsoleteAssertion? {
    val call = resolvedFunctionCall()?.takeIf { it.assertHint { name -> ASSERT_METHOD_PARAMETER_COUNT[name] } != null }
    return (call?.symbol as? KaNamedFunctionSymbol)?.obsoleteAssertion()
}

context(session: KaSession)
internal fun KtCallableReferenceExpression.obsoleteAssertion(): ObsoleteAssertion? {
    val symbol = with(session) { callableReference.mainReference.resolveToSymbol() } as? KaNamedFunctionSymbol
    val minimum = symbol?.run { ASSERT_METHOD_PARAMETER_COUNT[name.asString()] } ?: return null
    return symbol.takeIf { it.valueParameters.size >= minimum }?.obsoleteAssertion()
}

private fun KaNamedFunctionSymbol.obsoleteAssertion(): ObsoleteAssertion? {
    val owner = callableId?.classId?.takeIf { isStatic && it in OBSOLETE_ASSERTION_OWNERS } ?: return null
    val methodName = name.asString()
    return ObsoleteAssertion(
        "Call to '$methodName()' from '${owner.shortClassName.asString()}' should be replaced with a call to " +
            "a method from '${replacementFor(methodName).asFqNameString()}'",
    )
}

private fun replacementFor(methodName: String): ClassId =
    when {
        methodName == "assertThat" -> ClassId.fromString("org/hamcrest/MatcherAssert")
        methodName.startsWith("assume") -> JUPITER_ASSUMPTIONS
        else -> JUPITER_ASSERTIONS
    }
