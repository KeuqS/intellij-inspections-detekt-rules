package by.overpas.detekt.intellij.android.composepreview.previewmultipleparameterproviders

import by.overpas.detekt.intellij.android.composepreview.isPreviewOrMultiPreview
import by.overpas.detekt.intellij.android.composepreview.previewParameterAnnotation
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.secondPreviewParameter(): KtAnnotationEntry? =
    valueParameters.mapNotNull { it.previewParameterAnnotation() }
        .getOrNull(1)
        ?.takeIf { annotationEntries.any { entry -> entry.isPreviewOrMultiPreview() } }
