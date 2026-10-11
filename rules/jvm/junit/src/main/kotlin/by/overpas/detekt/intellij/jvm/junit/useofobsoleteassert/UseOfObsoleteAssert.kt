package by.overpas.detekt.intellij.jvm.junit.useofobsoleteassert

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.jvm.junit.JUNIT_FRAMEWORK_ASSERT
import by.overpas.detekt.intellij.jvm.junit.JUNIT_FRAMEWORK_TEST_CASE
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_ASSERT
import by.overpas.detekt.intellij.jvm.junit.isOnClasspath
import by.overpas.detekt.intellij.jvm.junit.ownerClassId
import by.overpas.detekt.intellij.jvm.junit.resolvedFunctionCall
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("UseOfObsoleteAssert")
class UseOfObsoleteAssert(config: Config) :
    Rule(
        config,
        "A call to an assertion of the JUnit 3 class `junit.framework.Assert` or `junit.framework.TestCase` " +
            "while JUnit 4 is available. Call the method of `org.junit.Assert`.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val owner = analyze(expression) {
            val call = expression.resolvedFunctionCall()
            val isStatic = (call?.symbol as? KaNamedFunctionSymbol)?.isStatic == true
            call?.ownerClassId?.takeIf {
                isStatic && it in OBSOLETE_ASSERT_OWNERS && ORG_JUNIT_ASSERT.isOnClasspath()
            }
        } ?: return
        report(
            Finding(
                Entity.from(callee),
                "Call to '${callee.text}()' from '${owner.asFqNameString()}' should be replaced with call to " +
                    "method from 'org.junit.Assert'",
            ),
        )
    }

    private companion object {
        val OBSOLETE_ASSERT_OWNERS = setOf(JUNIT_FRAMEWORK_ASSERT, JUNIT_FRAMEWORK_TEST_CASE)
    }
}
