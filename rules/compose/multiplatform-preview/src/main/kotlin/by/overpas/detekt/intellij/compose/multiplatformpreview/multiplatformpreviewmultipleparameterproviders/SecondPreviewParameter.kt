package by.overpas.detekt.intellij.compose.multiplatformpreview.multiplatformpreviewmultipleparameterproviders

import by.overpas.detekt.intellij.compose.multiplatformpreview.previewAnnotationEntries
import by.overpas.detekt.intellij.compose.multiplatformpreview.previewParameterAnnotation
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.secondPreviewParameter(): KtAnnotationEntry? =
    valueParameters.mapNotNull { it.previewParameterAnnotation() }
        .getOrNull(1)
        ?.takeIf { previewAnnotationEntries().isNotEmpty() }
