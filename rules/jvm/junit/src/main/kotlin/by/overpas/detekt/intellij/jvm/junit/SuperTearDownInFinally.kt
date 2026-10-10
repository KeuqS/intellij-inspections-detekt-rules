package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

@IntellijInspection("SuperTearDownInFinally")
class SuperTearDownInFinally(config: Config) :
    Rule(
        config,
        "A JUnit 3 `tearDown()` calls `super.tearDown()` outside of a `finally` block. If the code before it " +
            "throws, the parent clean-up does not run. Call `super.tearDown()` in a `finally` block.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val function = expression.superTearDownFunction() ?: return
        val containingClass = function.containingClassOrObject ?: return
        if (expression.isInFinallyBlock(function) || !function.hasOtherCalls(expression)) return
        if (analyze(containingClass) { containingClass.isSubclassOf(JUNIT_FRAMEWORK_TEST_CASE) }) {
            report(Finding(Entity.from(expression), "'tearDown()' is not called from 'finally' block"))
        }
    }
}
