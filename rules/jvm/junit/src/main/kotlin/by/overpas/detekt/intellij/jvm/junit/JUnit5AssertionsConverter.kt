package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

@IntellijInspection("JUnit5AssertionsConverter")
class JUnit5AssertionsConverter(config: Config) :
    Rule(
        config,
        "A JUnit 5 test calls an assertion or assumption of JUnit 3 or JUnit 4. " +
            "Call the method of `Assertions`, `Assumptions` or `MatcherAssert`.",
    ),
    RequiresAnalysisApi {

    private var isJUnit5File = false

    override fun visitKtFile(file: KtFile) {
        isJUnit5File = analyze(file) { file.isJUnit5AssertionsConverterCandidate() }
        if (isJUnit5File) super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val obsolete = analyze(expression) { expression.obsoleteAssertion() } ?: return
        reportObsolete(callee, obsolete)
    }

    override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression) {
        super.visitCallableReferenceExpression(expression)
        val obsolete = analyze(expression) { expression.obsoleteAssertion() } ?: return
        reportObsolete(expression, obsolete)
    }

    private fun reportObsolete(element: KtElement, assertion: ObsoleteAssertion) {
        report(
            Finding(
                Entity.from(element),
                "Call to '${assertion.methodName}()' from '${assertion.owner}' should be replaced with a call to " +
                    "a method from '${assertion.replacement}'",
            ),
        )
    }
}
