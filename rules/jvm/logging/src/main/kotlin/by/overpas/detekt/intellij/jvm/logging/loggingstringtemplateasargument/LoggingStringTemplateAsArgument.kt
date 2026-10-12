package by.overpas.detekt.intellij.jvm.logging.loggingstringtemplateasargument

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.jvm.logging.LimitLevelType
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("LoggingStringTemplateAsArgument")
class LoggingStringTemplateAsArgument(config: Config) :
    Rule(
        config,
        "A logging call gets a string template as its message, so the message is built even when the log level " +
            "is off. Use placeholders such as `{}` and pass the values as arguments.",
    ),
    RequiresAnalysisApi {

    @Configuration(
        "The log levels to report: `ALL`, `WARN_AND_LOWER`, `INFO_AND_LOWER`, `DEBUG_AND_LOWER` or `TRACE`.",
    )
    private val limitLevelType: LimitLevelType by config("DEBUG_AND_LOWER") { LimitLevelType.valueOf(it) }

    @Configuration("Do not report templates that hold only values of primitive types, their wrappers or strings.")
    private val arePrimitivesSkipped: Boolean by config(true)

    @Configuration("Do not report calls that pass only an exception after the message.")
    private val isOnlyExceptionSkipped: Boolean by config(false)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val arguments = StringTemplateArguments(limitLevelType, arePrimitivesSkipped, isOnlyExceptionSkipped)
        if (analyze(expression) { arguments.isReported(expression) }) {
            report(Finding(Entity.from(callee), "String template as argument to '${callee.text}()' logging call"))
        }
    }
}
