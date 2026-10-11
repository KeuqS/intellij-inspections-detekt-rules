package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

internal val KtLambdaExpression.returnedExpressions: List<KtExpression>
    get() = listOfNotNull(bodyExpression?.statements?.lastOrNull()?.takeIf { it !is KtReturnExpression }) +
        collectDescendantsOfType<KtReturnExpression>().mapNotNull { it.returnedExpression }
