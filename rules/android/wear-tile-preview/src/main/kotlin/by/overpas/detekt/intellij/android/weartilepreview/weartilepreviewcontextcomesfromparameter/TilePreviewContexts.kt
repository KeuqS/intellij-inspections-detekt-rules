package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcontextcomesfromparameter

import by.overpas.detekt.intellij.android.weartilepreview.CONTEXT
import by.overpas.detekt.intellij.android.weartilepreview.hasContextType
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

internal class TilePreviewContexts(private val function: KtNamedFunction) {

    context(session: KaSession)
    fun foreignArguments(): List<KtExpression> =
        function.collectDescendantsOfType<KtCallExpression>()
            .flatMap { call -> call.valueArguments.mapNotNull { it.getArgumentExpression() } }
            .filter { it.isForeign() }

    context(session: KaSession)
    fun foreignReceivers(): List<KtExpression> =
        function.collectDescendantsOfType<KtQualifiedExpression>()
            .asSequence()
            .filter { it.selectsAndroidDeclaration() }
            .map { it.receiverExpression }
            .filter { it.isForeign() }
            .toList()

    context(session: KaSession)
    private fun KtExpression.isForeign(): Boolean {
        val isContext = with(session) { expressionType?.run { withNullability(false).isSubtypeOf(CONTEXT) } == true }
        val contextParameter = function.valueParameters.firstOrNull { it.hasContextType() }
        val symbol = resolvedSymbol()
        return isContext && if (contextParameter == null) symbol != null else symbol?.psi != contextParameter
    }
}
