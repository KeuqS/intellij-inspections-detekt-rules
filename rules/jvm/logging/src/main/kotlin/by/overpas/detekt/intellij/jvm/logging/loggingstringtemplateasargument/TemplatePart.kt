package by.overpas.detekt.intellij.jvm.logging.loggingstringtemplateasargument

import by.overpas.detekt.intellij.jvm.logging.classIdOrNull
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtExpression

private val PRIMITIVE_OR_TEXT_CLASSES = buildSet {
    addAll(StandardClassIds.primitiveTypes)
    add(StandardClassIds.String)
    add(StandardClassIds.CharSequence)
}

internal data class TemplatePart(
    val expression: KtExpression?,
    val isLiteral: Boolean,
)

context(session: KaSession)
internal fun TemplatePart.isPrimitiveOrText(): Boolean =
    isLiteral || expression?.let { with(session) { it.expressionType } }?.classIdOrNull in PRIMITIVE_OR_TEXT_CLASSES
