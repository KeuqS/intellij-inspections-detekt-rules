package by.overpas.detekt.intellij.jvm.logging

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.parents

context(session: KaSession)
internal fun KtProperty.isReassigned(): Boolean {
    val function = parents.firstOrNull { it.isMethodLike() } ?: return true
    return function.collectDescendantsOfType<KtBinaryExpression> { assignment ->
        val left = assignment.left as? KtNameReferenceExpression
        assignment.operationToken in KtTokens.ALL_ASSIGNMENTS &&
            left?.getReferencedName() == name &&
            left?.resolvedSymbol()?.psi == this
    }.isNotEmpty()
}

private fun PsiElement.isMethodLike(): Boolean =
    this is KtDeclarationWithBody && this !is KtFunctionLiteral && !(this is KtNamedFunction && isLocal)
