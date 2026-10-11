package by.overpas.detekt.intellij.android.glancepreview.glancepreviewdimensionrespectslimit

import by.overpas.detekt.intellij.android.glancepreview.isPreview
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtValueArgument

private val DIMENSION_PARAMETERS = listOf("widthDp", "heightDp")
private val DIMENSION_LIMITS = 1..3000

context(session: KaSession)
internal fun KtAnnotationEntry.dimensionOutOfLimits(): KtValueArgument? {
    if (!isPreview()) return null
    val arguments = valueArguments.filterIsInstance<KtValueArgument>()
    return DIMENSION_PARAMETERS
        .mapNotNull { parameter ->
            arguments.firstOrNull { it.getArgumentName()?.run { asName.asString() } == parameter }
        }
        .lastOrNull { it.isOutOfLimits() }
}

context(session: KaSession)
private fun KtValueArgument.isOutOfLimits(): Boolean {
    val dimension = with(session) { getArgumentExpression()?.evaluate()?.value } as? Int
    return dimension != null && dimension !in DIMENSION_LIMITS
}
