package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcontextcomesfromparameter

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression

context(session: KaSession)
internal fun KtQualifiedExpression.selectsAndroidDeclaration(): Boolean {
    val selector = selectorExpression
    val callee = if (selector is KtCallExpression) selector.calleeExpression else selector
    val reference = callee as? KtReferenceExpression
    val symbol = with(session) { reference?.run { mainReference.resolveToSymbol() } } as? KaCallableSymbol
    val name = symbol?.callableId?.run { asSingleFqName().asString() }.orEmpty()
    return name.startsWith("android.") || name.startsWith("androidx.")
}
