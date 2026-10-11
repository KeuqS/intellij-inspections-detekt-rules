package by.overpas.detekt.intellij.jvm.logging.logstatementnotguardedbylogcondition

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtReturnExpression

internal fun KtExpression.isImplicitlyReturned(): Boolean =
    when (val parent = parent) {
        is KtReturnExpression -> true
        is KtBlockExpression -> parent.parent is KtFunctionLiteral && parent.statements.lastOrNull() == this
        is KtDeclarationWithBody -> parent.bodyExpression == this
        else -> false
    }
