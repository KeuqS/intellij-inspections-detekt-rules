package by.overpas.detekt.intellij.jvm.logging

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("LoggingConditionDisagreesWithLogLevelStatement")
class LoggingConditionDisagreesWithLogLevelStatement(config: Config) :
    Rule(
        config,
        "A log level condition such as `isDebugEnabled` checks another level than the logging call it guards. " +
            "Make the level of the condition and the level of the call the same.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val disagreement = analyze(expression) { expression.levelDisagreement() } ?: return
        report(
            Finding(
                Entity.from(disagreement.anchor),
                "Level of condition '${disagreement.conditionLevel}' does not match level of logging call " +
                    "'${disagreement.callLevel}'",
            ),
        )
    }
}
