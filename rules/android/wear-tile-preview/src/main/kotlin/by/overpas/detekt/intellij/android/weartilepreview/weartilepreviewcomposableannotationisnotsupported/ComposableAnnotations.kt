package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcomposableannotationisnotsupported

import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewAnnotation
import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewSignature
import by.overpas.detekt.intellij.android.weartilepreview.resolvedClassId
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

private val COMPOSABLE = ClassId.fromString("androidx/compose/runtime/Composable")

context(session: KaSession)
internal fun KtNamedFunction.composableOnTilePreview(): KtAnnotationEntry? =
    annotationEntries.firstOrNull { it.resolvedClassId() == COMPOSABLE }
        ?.takeIf { hasTilePreviewSignature() && hasTilePreviewAnnotation() }
