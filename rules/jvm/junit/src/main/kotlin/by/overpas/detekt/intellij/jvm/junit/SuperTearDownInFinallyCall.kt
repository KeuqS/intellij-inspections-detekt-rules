package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parents

private const val TEAR_DOWN = "tearDown"

internal fun KtCallExpression.superTearDownFunction(): KtNamedFunction? {
    val qualified = parent as? KtDotQualifiedExpression
    val isSuperTearDown = calleeExpression?.text == TEAR_DOWN &&
        qualified?.selectorExpression == this &&
        qualified.receiverExpression is KtSuperExpression
    val owner = getStrictParentOfType<KtDeclaration>()
    return (owner as? KtNamedFunction)?.takeIf {
        isSuperTearDown && it.name == TEAR_DOWN && owner !is KtFunctionLiteral
    }
}

internal fun KtCallExpression.isInFinallyBlock(function: KtNamedFunction): Boolean =
    parents.takeWhile { it != function }.any { it is KtFinallySection }

internal fun KtNamedFunction.hasOtherCalls(call: KtCallExpression): Boolean =
    collectDescendantsOfType<KtCallExpression>().any { it != call }
