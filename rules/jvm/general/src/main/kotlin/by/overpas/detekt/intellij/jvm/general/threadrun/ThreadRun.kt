package by.overpas.detekt.intellij.jvm.general.threadrun

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("ThreadRun")
class ThreadRun(config: Config) :
    Rule(
        config,
        "A call of `Thread.run()` runs the code in the current thread and does not start a new thread. " +
            "Call `start()` instead.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val isCandidate =
            callee.text == "run" && expression.valueArguments.isEmpty() && !expression.isThreadRunOnSuper()
        if (isCandidate && analyze(expression) { expression.isThreadRun() }) {
            report(Finding(Entity.from(callee), "Calls to 'run()' should probably be replaced with 'start()'"))
        }
    }
}
