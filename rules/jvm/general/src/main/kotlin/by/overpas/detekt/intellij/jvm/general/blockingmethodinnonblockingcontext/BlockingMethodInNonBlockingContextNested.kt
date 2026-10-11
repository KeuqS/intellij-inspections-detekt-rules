package by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.allChildren

internal class BlockingMethodInNonBlockingContextNested(private val root: KtExpression) {

    private val symbols = BlockingMethodInNonBlockingContextSymbols()

    context(session: KaSession)
    fun dispatcher(): BlockingMethodInNonBlockingContextDispatcher =
        visit(root, BlockingMethodInNonBlockingContextDispatcher.NO_INFORMATION)

    context(session: KaSession)
    private fun visit(
        element: PsiElement,
        found: BlockingMethodInNonBlockingContextDispatcher,
    ): BlockingMethodInNonBlockingContextDispatcher {
        val own = (element as? KtExpression)?.let { symbols.resolve(it) }?.let { symbols.dispatcherOf(it) }
        val current = own?.takeIf { it > found } ?: found
        return if (own != null && current.isDefinite()) {
            current
        } else {
            element.allChildren.fold(current) { result, child -> visit(child, result) }
        }
    }
}
