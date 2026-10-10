package by.overpas.detekt.intellij.jvm.testframeworks

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("AssertBetweenInconvertibleTypes")
class AssertBetweenInconvertibleTypes(config: Config) :
    Rule(
        config,
        "An equality or identity assertion compares objects of inconvertible types. " +
            "An equality assertion always fails and a non-equality assertion always passes.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val methodName = callee.text
        val (left, right) = when (methodName) {
            in ASSERT_METHOD_NAMES -> analyze(expression) { expression.assertInconvertibleTypes() }
            in ASSERTJ_METHOD_NAMES -> analyze(expression) { expression.assertJInconvertibleTypes() }
            else -> null
        } ?: return
        val message = when (methodName) {
            "assertNotEquals", "isNotEqualTo" ->
                "Possibly redundant assertion: incompatible types are compared '$left' and '$right'"

            "assertNotSame", "isNotSameAs" ->
                "Redundant assertion: incompatible types are compared '$left' and '$right'"

            else -> "'$methodName()' between objects of inconvertible types '$left' and '$right'"
        }
        report(Finding(Entity.from(callee), message))
    }

    private companion object {
        val ASSERT_METHOD_NAMES = setOf("assertEquals", "assertNotEquals", "assertSame", "assertNotSame")
        val ASSERTJ_METHOD_NAMES = setOf("isEqualTo", "isNotEqualTo", "isSameAs", "isNotSameAs")
    }
}
