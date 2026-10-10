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

internal class JUnitAssertHint(
    val call: KaFunctionCall<*>,
    val isMessageOnFirstPosition: Boolean,
    private val argumentIndex: Int,
) {
    private val parameters: List<KaValueParameterSymbol> = call.symbol.valueParameters

    val firstArgument: KtExpression? get() = argumentFor(argumentIndex)

    val secondArgument: KtExpression? get() = argumentFor(argumentIndex + 1)

    fun parameterAt(offset: Int): KaValueParameterSymbol? = parameters.getOrNull(argumentIndex + offset)

    private fun argumentFor(index: Int): KtExpression? {
        val parameter = parameters.getOrNull(index) ?: return null
        return call.valueArgumentMapping.entries.firstOrNull { it.value.symbol == parameter }?.key
    }
}

context(session: KaSession)
internal fun KaFunctionCall<*>.assertHint(minimumParameterCount: (String) -> Int?): JUnitAssertHint? {
    val name = functionName ?: return null
    val minimum = minimumParameterCount(name) ?: return null
    val owner = ownerClassId
    val messageOnFirst = owner in MESSAGE_FIRST_OWNERS
    val parameters = symbol.valueParameters
    val messageIndex = when {
        !messageOnFirst && owner !in MESSAGE_LAST_OWNERS -> return null
        parameters.size < minimum -> return null
        messageOnFirst -> if (parameters.firstOrNull().isString() && parameters.size > minimum) 1 else 0
        else -> 0
    }
    return JUnitAssertHint(this, messageOnFirst, messageIndex)
}

context(session: KaSession)
internal fun KaFunctionCall<*>.assertEqualsHint(): JUnitAssertHint? =
    assertHint { if (it == "assertEquals") 2 else null }

context(session: KaSession)
private fun KaValueParameterSymbol?.isString(): Boolean =
    with(session) { this@isString?.returnType?.lowerBoundIfFlexible()?.isStringType == true }
