package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

internal class TemplateStringParts(private val evaluator: StringPartEvaluator) {

    context(session: KaSession)
    fun evaluate(
        template: KtStringTemplateExpression,
        depth: Int,
        maxParts: Int,
    ): List<PartHolder> {
        if (!template.hasInterpolation()) return listOf(PartHolder(template.literalText, isConstant = true))
        val entries = template.entries
        val parts = entries.fold(emptyList<PartHolder>()) { result, entry ->
            when {
                result.size >= maxParts -> result

                entry is KtStringTemplateEntryWithExpression ->
                    result + evaluator.evaluate(entry.expression, depth, maxParts - entries.size + 1)

                else -> result + PartHolder(entry.literalText, isConstant = true)
            }
        }
        return if (parts.size >= maxParts) parts + PartHolder.UNKNOWN else parts
    }
}
