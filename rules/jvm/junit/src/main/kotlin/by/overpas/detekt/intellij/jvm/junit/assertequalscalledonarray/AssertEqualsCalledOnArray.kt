package by.overpas.detekt.intellij.jvm.junit.assertequalscalledonarray

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("AssertEqualsCalledOnArray")
class AssertEqualsCalledOnArray(config: Config) :
    Rule(
        config,
        "`assertEquals()` compares two arrays by identity, not by content. Use `assertArrayEquals()`.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        if (callee.text != "assertEquals") return
        if (analyze(expression) { expression.isAssertEqualsOnArrays() }) {
            report(Finding(Entity.from(callee), "'assertEquals()' called on array"))
        }
    }
}
