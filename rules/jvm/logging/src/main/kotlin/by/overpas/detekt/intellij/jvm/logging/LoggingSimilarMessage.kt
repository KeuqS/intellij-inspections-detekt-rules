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
import org.jetbrains.kotlin.psi.KtFile

@IntellijInspection("LoggingSimilarMessage")
class LoggingSimilarMessage(config: Config) :
    Rule(
        config,
        "Two logging calls in the same file have messages that are so similar that their log records are hard " +
            "to tell apart. Make the messages different.",
    ),
    RequiresAnalysisApi {

    @Configuration("Do not report calls with the `error` log level or with an exception.")
    private val isErrorLogLevelSkipped: Boolean by config(true)

    @Configuration("The minimum length of a similar text sequence.")
    private val minTextLength: Int by config(5)

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        val pairs = analyze(file) { SimilarLogCalls(isErrorLogLevelSkipped, minTextLength).find(file) }
        pairs.forEach { (call, other) ->
            report(Finding(Entity.from(call), "Similar log messages", references = listOf(Entity.from(other))))
        }
    }
}
