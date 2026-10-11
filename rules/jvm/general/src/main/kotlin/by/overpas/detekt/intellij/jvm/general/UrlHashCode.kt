package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

@IntellijInspection("UrlHashCode")
class UrlHashCode(config: Config) :
    Rule(
        config,
        "`equals()` and `hashCode()` of `java.net.URL` can resolve the host name over the network, which is slow. " +
            "Hash-based collections of URLs call them too. Use `java.net.URI` instead.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        val call = UrlHashCodeCall(expression)
        val method = analyze(expression) { call.urlMethod() }
        if (method != null) {
            report(Finding(Entity.from(callee), "Call to '$method()' on URL object"))
        } else if (analyze(expression) { call.isUrlCollectionOperation() }) {
            val receiver = call.receiver()
            val anchor = receiver ?: callee
            report(Finding(Entity.from(anchor), "'${receiver?.text ?: "this"}' may contain URL objects"))
        }
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        if (expression.operationToken == KtTokens.EQEQ && analyze(expression) { expression.isUrlEquality() }) {
            report(Finding(Entity.from(expression.operationReference), "Call to 'equals()' on URL object"))
        }
    }

    override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression) {
        super.visitCallableReferenceExpression(expression)
        val method = analyze(expression) { expression.urlMethodReference() } ?: return
        report(Finding(Entity.from(expression), "Call to '$method()' on URL object"))
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        UrlHashCodeDeclaration(property).finding()?.let(::report)
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        UrlHashCodeDeclaration(parameter).finding()?.let(::report)
    }
}
