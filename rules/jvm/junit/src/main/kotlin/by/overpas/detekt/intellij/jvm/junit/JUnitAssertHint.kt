package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.psi.KtExpression

private val MESSAGE_FIRST_OWNERS = setOf(
    TESTNG_ASSERT_JUNIT,
    JUNIT_FRAMEWORK_ASSERT,
    ORG_JUNIT_ASSERT,
    JUNIT_FRAMEWORK_TEST_CASE,
    ORG_JUNIT_ASSUME,
)

private val MESSAGE_LAST_OWNERS = setOf(TESTNG_ASSERT, JUPITER_ASSERTIONS, JUPITER_ASSUMPTIONS)

internal val ASSERT_METHOD_PARAMETER_COUNT = mapOf(
    "assertArrayEquals" to 2,
    "assertEquals" to 2,
    "assertNotEquals" to 2,
    "assertFalse" to 1,
    "assumeFalse" to 1,
    "assertNotNull" to 1,
    "assertNotSame" to 2,
    "assertNull" to 1,
    "assertSame" to 2,
    "assertThat" to 2,
    "assertTrue" to 1,
    "assumeTrue" to 1,
    "fail" to 0,
    "assertEqualsNoOrder" to 2,
)

internal data class JUnitAssertHint(
    val firstArgument: KtExpression?,
    val secondArgument: KtExpression?,
)

context(session: KaSession)
internal fun KaFunctionCall<*>.assertHint(minimumParameterCount: (String) -> Int?): JUnitAssertHint? {
    val minimum = functionName?.let(minimumParameterCount)
    val owner = ownerClassId
    val isMessageOnFirst = owner in MESSAGE_FIRST_OWNERS
    val parameters = symbol.valueParameters
    val isAssertion = minimum != null &&
        (isMessageOnFirst || owner in MESSAGE_LAST_OWNERS) &&
        parameters.size >= minimum
    val hasLeadingMessage = isMessageOnFirst && parameters.firstOrNull().isString() && parameters.size > (minimum ?: 0)
    val index = if (hasLeadingMessage) 1 else 0
    return JUnitAssertHint(argumentAt(index), argumentAt(index + 1)).takeIf { isAssertion }
}

context(session: KaSession)
private fun KaValueParameterSymbol?.isString(): Boolean =
    with(session) { this@isString?.run { returnType.lowerBoundIfFlexible().isStringType } == true }
