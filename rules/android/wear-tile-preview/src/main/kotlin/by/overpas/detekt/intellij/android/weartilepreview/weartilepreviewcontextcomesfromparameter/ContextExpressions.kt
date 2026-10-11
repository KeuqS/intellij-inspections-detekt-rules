package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcontextcomesfromparameter

import by.overpas.detekt.intellij.android.weartilepreview.CONTEXT
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtQualifiedExpression

context(session: KaSession)
internal fun KtExpression.isForeignContext(contextParameter: KtParameter?): Boolean {
    val isContext = with(session) { expressionType?.run { withNullability(false).isSubtypeOf(CONTEXT) } == true }
    val symbol = resolvedSymbol()
    return isContext && if (contextParameter == null) symbol != null else symbol?.psi != contextParameter
}

context(session: KaSession)
private fun KtExpression.resolvedSymbol(): KaSymbol? =
    when (this) {
        is KtQualifiedExpression -> selectorExpression?.resolvedSymbol()
        is KtCallExpression -> calleeExpression?.resolvedSymbol()
        is KtNameReferenceExpression -> with(session) { mainReference.resolveToSymbol() }
        else -> null
    }
