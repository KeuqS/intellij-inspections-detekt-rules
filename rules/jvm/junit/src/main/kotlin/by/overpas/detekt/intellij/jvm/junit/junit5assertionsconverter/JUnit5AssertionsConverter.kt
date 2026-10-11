package by.overpas.detekt.intellij.jvm.junit.junit5assertionsconverter

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtFile

@IntellijInspection("JUnit5AssertionsConverter")
class JUnit5AssertionsConverter(config: Config) :
    Rule(
        config,
        "A JUnit 5 test calls an assertion or assumption of JUnit 3 or JUnit 4. " +
            "Call the method of `Assertions`, `Assumptions` or `MatcherAssert`.",
    ),
    RequiresAnalysisApi {

    override fun visitKtFile(file: KtFile) {
        if (analyze(file) { file.hasJUnit5TestClass() }) super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val (message) = analyze(expression) { expression.obsoleteAssertion() } ?: return
        report(Finding(Entity.from(callee), message))
    }

    override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression) {
        super.visitCallableReferenceExpression(expression)
        val (message) = analyze(expression) { expression.obsoleteAssertion() } ?: return
        report(Finding(Entity.from(expression), message))
    }
}
