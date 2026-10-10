package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class TestMethodWithoutAssertionMatcher(
    private val methods: List<TestMethodWithoutAssertionMethod>,
    private val isAssertKeywordAssertion: Boolean,
) {

    context(session: KaSession)
    fun matches(function: KaFunctionSymbol): Boolean {
        val name = function.assertionName()
        val owners = function.assertionOwners()
        return if (function.callableId == KOTLIN_ASSERT) {
            isAssertKeywordAssertion
        } else {
            name != null && methods.any { (owner, pattern) -> owner in owners && pattern.matches(name) }
        }
    }

    private companion object {
        val KOTLIN_ASSERT = CallableId(FqName("kotlin"), Name.identifier("assert"))
    }
}
