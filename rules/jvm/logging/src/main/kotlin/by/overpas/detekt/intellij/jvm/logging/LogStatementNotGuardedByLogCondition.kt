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
import org.jetbrains.kotlin.psi.KtCallExpression

@OppositeRule(LoggingGuardedByCondition::class)
@IntellijInspection("LogStatementNotGuardedByLogCondition")
class LogStatementNotGuardedByLogCondition(config: Config) :
    Rule(
        config,
        "A logging call with computed arguments is not inside a log level condition such as " +
            "`if (logger.isDebugEnabled)`, so the arguments are computed even when the level is off. " +
            "Surround the call with a log level condition.",
    ),
    RequiresAnalysisApi {

    @Configuration(
        "The log levels to report: `ALL`, `WARN_AND_LOWER`, `INFO_AND_LOWER`, `DEBUG_AND_LOWER` or `TRACE`.",
    )
    private val limitLevelType: LimitLevelType by config("DEBUG_AND_LOWER") { LimitLevelType.valueOf(it) }

    @Configuration("Also report logging calls that have only constant arguments.")
    private val isUnguardedConstantFlagged: Boolean by config(false)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val calls = UnguardedLogCalls(limitLevelType, LogCallArguments(isUnguardedConstantFlagged))
        if (analyze(expression) { calls.isUnguarded(expression) }) {
            report(
                Finding(
                    Entity.from(expression.qualifiedOrThis),
                    "Logging call not guarded by a logging condition",
                ),
            )
        }
    }
}
