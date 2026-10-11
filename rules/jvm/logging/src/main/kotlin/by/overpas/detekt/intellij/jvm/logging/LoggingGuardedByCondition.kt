package by.overpas.detekt.intellij.jvm.logging

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.OppositeRule
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtIfExpression

@OppositeRule(LogStatementNotGuardedByLogCondition::class)
@IntellijInspection("LoggingGuardedByCondition")
class LoggingGuardedByCondition(config: Config) :
    Rule(
        config,
        "A logging call is inside a log level condition such as `if (logger.isDebugEnabled)`. " +
            "The logging framework checks the level itself, so remove the condition.",
    ),
    RequiresAnalysisApi {

    @Configuration("Report only conditions that hold nothing but the guarded logging calls, so they can be unwrapped.")
    private val isReportedOnlyIfFixPossible: Boolean by config(true)

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        val guard = analyze(expression) { expression.redundantLogGuard(isReportedOnlyIfFixPossible) } ?: return
        report(Finding(Entity.from(guard), "Logging call guarded by log condition"))
    }
}
