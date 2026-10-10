package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("MigrateAssertToMatcherAssert")
class MigrateAssertToMatcherAssert(config: Config) :
    Rule(
        config,
        "A JUnit assertion such as `assertEquals()` or `assertTrue()` while Hamcrest is available. " +
            "Use `assertThat()` with a Hamcrest matcher.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        if (callee.text !in MATCHER_ASSERT_METHODS) return
        if (analyze(expression) { expression.isMatcherAssertCandidate() }) {
            report(
                Finding(
                    Entity.from(callee),
                    "Assert expression '${callee.text}' can be replaced with 'assertThat()' call",
                ),
            )
        }
    }
}
