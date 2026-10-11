package by.overpas.detekt.intellij.jvm.declarationredundancy

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.parents

internal fun KtReturnExpression.returnsFrom(function: KtNamedFunction): Boolean {
    val label = getLabelName()
    return parents.takeWhile { it !is KtClassOrObject }.firstOrNull { it.isReturnTarget(label) } == function
}

private fun PsiElement.isReturnTarget(label: String?): Boolean =
    when {
        this !is KtDeclarationWithBody -> false
        label == null -> this !is KtFunctionLiteral
        this is KtFunctionLiteral -> label in labels()
        else -> (this as? KtNamedFunction)?.name == label
    }
