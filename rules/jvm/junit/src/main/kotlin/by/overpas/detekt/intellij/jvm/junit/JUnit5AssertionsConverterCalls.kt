package by.overpas.detekt.intellij.jvm.junit

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

internal class ObsoleteAssertion(val methodName: String, val owner: String, val replacement: String)

context(session: KaSession)
internal fun KtFile.isJUnit5AssertionsConverterCandidate(): Boolean =
    ORG_JUNIT_TEST.isOnClasspath() &&
        JUPITER_TEST.isOnClasspath() &&
        collectDescendantsOfType<KtClassOrObject>().any { it.isJUnit5TestClass() }

context(session: KaSession)
internal fun KtCallExpression.obsoleteAssertion(): ObsoleteAssertion? {
    val call = resolvedFunctionCall() ?: return null
    call.assertHint { ASSERT_METHOD_PARAMETER_COUNT[it] } ?: return null
    return (call.symbol as? KaNamedFunctionSymbol)?.obsoleteAssertion()
}

context(session: KaSession)
internal fun KtCallableReferenceExpression.obsoleteAssertion(): ObsoleteAssertion? {
    val symbol = with(session) { callableReference.mainReference.resolveToSymbol() } as? KaNamedFunctionSymbol
    val minimum = symbol?.name?.asString()?.let { ASSERT_METHOD_PARAMETER_COUNT[it] } ?: return null
    return symbol.takeIf { it.valueParameters.size >= minimum }?.obsoleteAssertion()
}

private fun KaNamedFunctionSymbol.obsoleteAssertion(): ObsoleteAssertion? {
    val owner = callableId?.classId?.takeIf { isStatic && it in OBSOLETE_ASSERTION_OWNERS } ?: return null
    val methodName = name.asString()
    return ObsoleteAssertion(methodName, owner.shortClassName.asString(), replacementFor(methodName).asFqNameString())
}

private fun replacementFor(methodName: String): ClassId =
    when {
        methodName == "assertThat" -> classId("org.hamcrest.MatcherAssert")
        methodName.startsWith("assume") -> JUPITER_ASSUMPTIONS
        else -> JUPITER_ASSERTIONS
    }
