package by.overpas.detekt.intellij.jvm.junit.superteardowninfinally

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.jvm.junit.isJUnit3TestCase
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
        val function = expression.superTearDownFunction()
            ?.takeIf { !expression.isInFinallyBlock(it) && it.hasOtherCalls(expression) }
        val containingClass = function?.containingClassOrObject ?: return
        if (analyze(containingClass) { containingClass.isJUnit3TestCase() }) {
            report(Finding(Entity.from(expression), "'tearDown()' is not called from 'finally' block"))
        }
    }
}
