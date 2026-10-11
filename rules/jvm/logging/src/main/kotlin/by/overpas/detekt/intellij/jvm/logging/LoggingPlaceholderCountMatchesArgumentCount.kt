package by.overpas.detekt.intellij.jvm.logging

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("LoggingPlaceholderCountMatchesArgumentCount")
class LoggingPlaceholderCountMatchesArgumentCount(config: Config) :
    Rule(
        config,
        "The number of placeholders in the message of a logging call does not match the number of arguments. " +
            "Add or remove placeholders or arguments.",
    ),
    RequiresAnalysisApi {

    @Configuration(
        "Whether SLF4J uses Log4j 2 as its implementation: `AUTO` checks the classpath, `YES` or `NO` sets it.",
    )
    private val slf4jToLog4J2Type: String by config("AUTO")

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val isSlf4jOnLog4j = when (slf4jToLog4J2Type) {
            "YES" -> true
            "NO" -> false
            else -> null
        }
        val mismatch = analyze(expression) {
            expression.placeholderContext(isSlf4jOnLog4j)?.let { it.logStringArgument to it.mismatchMessage() }
        }
        val (argument, message) = mismatch ?: return
        if (message != null) report(Finding(Entity.from(argument), message))
    }
}
