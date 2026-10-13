package by.overpas.detekt.intellij.kotlin.redundantconstructs.explicitthis

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

internal class ExplicitThisUnqualified(private val qualified: KtDotQualifiedExpression) {

    fun keepsTarget(): Boolean {
        val selector = qualified.selectorExpression
        val unqualified = selector?.let {
            KtPsiFactory(qualified.project).createExpressionCodeFragment(it.text, qualified).getContentElement()
        }
        val explicit = selector?.let { analyze(it) { ExplicitThisCall(it).target() } }
        return explicit != null &&
            unqualified != null &&
            explicit == analyze(unqualified) { ExplicitThisCall(unqualified).target() }
    }
}
