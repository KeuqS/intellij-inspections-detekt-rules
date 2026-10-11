package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCollectionLiteralExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

internal val SUPPRESSION_ANNOTATION_NAMES = setOf("Suppress", "SuppressWarnings")

private val SUPPRESSION_ANNOTATIONS = setOf(
    StandardClassIds.Annotations.Suppress,
    ClassId.fromString("java/lang/SuppressWarnings"),
)

private val ARRAY_OF = CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("arrayOf"))

internal class SuppressionAnnotationIds(private val entry: KtAnnotationEntry) {

    context(session: KaSession)
    fun ids(): List<String>? =
        with(session) {
            val classId = (entry.typeReference?.type as? KaClassType)?.classId
            entry.valueArguments
                .asSequence()
                .mapNotNull { it.getArgumentExpression() }
                .flatMap { flatten(it) }
                .mapNotNull { it.evaluate()?.value as? String }
                .toList()
                .takeIf { classId in SUPPRESSION_ANNOTATIONS }
        }

    context(session: KaSession)
    private fun flatten(expression: KtExpression): List<KtExpression> =
        with(session) {
            val arguments = (expression as? KtCallExpression)
                ?.takeIf { it.resolveToCall()?.successfulFunctionCallOrNull()?.run { symbol.callableId } == ARRAY_OF }
                ?.valueArguments
                .orEmpty()
                .mapNotNull { it.getArgumentExpression() }
            when (expression) {
                is KtCollectionLiteralExpression -> expression.innerExpressions.flatMap { flatten(it) }
                is KtCallExpression -> arguments.flatMap { flatten(it) }
                is KtStringTemplateExpression, is KtSimpleNameExpression, is KtQualifiedExpression -> listOf(expression)
                else -> emptyList()
            }
        }
}
