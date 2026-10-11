package by.overpas.detekt.intellij.jvm.junit.assertequalsmaybeassertsame

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("AssertEqualsMayBeAssertSame")
class AssertEqualsMayBeAssertSame(config: Config) :
    Rule(
        config,
        "`assertEquals()` compares two objects of a final class that does not override `equals()`, " +
            "so it checks identity. Use `assertSame()` to make this clear.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        if (callee.text != "assertEquals") return
        if (analyze(expression) { expression.isAssertEqualsOnIdentity() }) {
            report(Finding(Entity.from(callee), "'assertEquals()' may be 'assertSame()'"))
        }
    }
}
