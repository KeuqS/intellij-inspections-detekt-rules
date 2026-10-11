package by.overpas.detekt.intellij.android.composepreview.composepreviewdimensionrespectslimit

import by.overpas.detekt.intellij.android.composepreview.constantValue
import by.overpas.detekt.intellij.android.composepreview.namedArgument
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtValueArgument

private val DIMENSION_PARAMETERS = listOf("widthDp", "heightDp")

private val DIMENSION_LIMITS = 1..3000

context(session: KaSession)
internal fun KtAnnotationEntry.lastDimensionOutOfLimits(): KtValueArgument? =
    DIMENSION_PARAMETERS.mapNotNull { namedArgument(it) }
        .lastOrNull { argument -> (argument.constantValue() as? Int)?.let { it !in DIMENSION_LIMITS } == true }
