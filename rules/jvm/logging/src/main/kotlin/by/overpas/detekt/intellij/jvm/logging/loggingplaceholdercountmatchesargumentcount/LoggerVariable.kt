package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import by.overpas.detekt.intellij.jvm.logging.resolvedSymbol
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

internal class LoggerVariable(private val symbol: KaVariableSymbol) {

    private val declaration = symbol.psi as? KtCallableDeclaration

    val isFinal: Boolean
        get() = when (declaration) {
            is KtProperty -> !declaration.isLocal && !declaration.isVar
            is KtParameter -> !declaration.isMutable
            else -> false
        }

    context(session: KaSession)
    fun initializers(): List<KtExpression>? {
        val property = declaration as? KtProperty
        val initializer = property?.initializer
        return when {
            initializer != null -> listOf(KtPsiUtil.safeDeparenthesize(initializer))

            property == null -> null

            property.isLocal -> emptyList()

            else -> (property.containingClassOrObject ?: property.containingKtFile)
                .collectDescendantsOfType<KtBinaryExpression> { it.isAssignmentOf(property) }
                .mapNotNull { it.right?.let(KtPsiUtil::safeDeparenthesize) }
        }
    }

    context(session: KaSession)
    private fun KtBinaryExpression.isAssignmentOf(property: KtProperty): Boolean {
        val left = left as? KtNameReferenceExpression ?: return false
        return operationToken == KtTokens.EQ && left.text == property.name && left.resolvedSymbol() == symbol
    }
}
