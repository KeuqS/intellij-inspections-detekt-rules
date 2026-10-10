package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

context(session: KaSession)
internal fun KtExpression.isTrivialStatement(): Boolean =
    when (this) {
        is KtBlockExpression -> statements.all { it.isTrivialStatement() }
        is KtIfExpression -> isTrivialIf()
        is KtReturnExpression -> returnedExpression == null
        is KtThrowExpression -> true
        is KtBinaryExpression -> isAssignmentToVal()
        else -> false
    }
