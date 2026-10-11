package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

private const val MAX_DEPTH = 10
private const val MAX_PARTS = 20

private val STRING_CLASSES = setOf(StandardClassIds.String, StandardClassIds.Char)

internal class StringPartEvaluator {

    context(session: KaSession)
    fun evaluate(
        expression: KtExpression?,
        depth: Int,
        maxParts: Int,
    ): List<PartHolder> {
        val currentDepth = depth - 1
        val isWithinLimits = maxParts > 0 && currentDepth > 0
        val unwrapped = expression?.run { KtPsiUtil.safeDeparenthesize(this).takeIf { isWithinLimits } }
        return when {
            unwrapped == null -> listOf(PartHolder.UNKNOWN)

            !unwrapped.isStringLike() -> listOf(PartHolder.NON_TEXT_CONSTANT)

            unwrapped is KtStringTemplateExpression ->
                TemplateStringParts(this).evaluate(unwrapped, currentDepth, maxParts)

            unwrapped is KtConstantExpression ->
                listOf(PartHolder(with(session) { unwrapped.evaluate() }?.value?.toString(), isConstant = true))

            unwrapped is KtBinaryExpression ->
                ConcatenationStringParts(this).evaluate(unwrapped, currentDepth, maxParts)

            unwrapped is KtNameReferenceExpression || unwrapped is KtDotQualifiedExpression ->
                ReferenceStringParts(this).evaluate(unwrapped, currentDepth, maxParts)

            unwrapped is KtCallExpression -> CallStringParts(this).evaluate(unwrapped, currentDepth, maxParts)

            else -> listOf(PartHolder.UNKNOWN)
        }
    }
}

context(session: KaSession)
internal fun KtExpression.stringParts(): List<PartHolder>? =
    takeIf { it.isStringLike() }?.let { StringPartEvaluator().evaluate(it, MAX_DEPTH, MAX_PARTS).joined() }

context(session: KaSession)
internal fun KtExpression.isStringLike(): Boolean =
    with(session) { expressionType }?.classIdOrNull in STRING_CLASSES
