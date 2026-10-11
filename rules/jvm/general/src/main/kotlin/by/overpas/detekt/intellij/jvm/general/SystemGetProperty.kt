package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("SystemGetProperty")
class SystemGetProperty(config: Config) :
    Rule(
        config,
        "A `System.getProperty()` call reads a property that a dedicated API gives directly, such as " +
            "`System.lineSeparator()` or `File.pathSeparator`. Use that API instead.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val isCandidate = callee.text == "getProperty" && expression.valueArguments.size == 1
        val property = expression.takeIf { isCandidate }?.let { analyze(it) { it.simplifiableSystemProperty() } }
        if (property != null) {
            report(Finding(Entity.from(callee), "Call 'getProperty' can be simplified for '$property'"))
        }
    }
}
