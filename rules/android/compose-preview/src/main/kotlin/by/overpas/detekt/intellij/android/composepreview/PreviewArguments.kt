package by.overpas.detekt.intellij.android.composepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.base.KaConstantValue
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtValueArgument

context(session: KaSession)
internal fun KtDeclaration.directPreviews(): List<KtAnnotationEntry> =
    when (this) {
        is KtNamedFunction -> annotationEntries.filter { it.isPreview() }
        is KtClass if isAnnotation() -> annotationEntries.filter { it.isPreview() }
        else -> emptyList()
    }

internal fun KtAnnotationEntry.namedArgument(name: String): KtValueArgument? =
    valueArguments.filterIsInstance<KtValueArgument>()
        .firstOrNull { it.getArgumentName()?.run { asName.asString() } == name }

context(session: KaSession)
internal fun KtValueArgument.constantValue(): Any? =
    when (val constant = with(session) { getArgumentExpression()?.evaluate() }) {
        null, is KaConstantValue.ErrorValue -> null
        else -> constant.value
    }
