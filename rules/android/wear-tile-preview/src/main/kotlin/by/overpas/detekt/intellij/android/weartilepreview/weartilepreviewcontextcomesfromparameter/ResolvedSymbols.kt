package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcontextcomesfromparameter

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

context(session: KaSession)
internal fun KtExpression.resolvedSymbol(): KaSymbol? =
    when (this) {
        is KtQualifiedExpression -> selectorExpression?.resolvedSymbol()
        is KtCallExpression -> calleeExpression?.resolvedSymbol()
        is KtNameReferenceExpression -> with(session) { mainReference.resolveToSymbol() }
        else -> null
    }
