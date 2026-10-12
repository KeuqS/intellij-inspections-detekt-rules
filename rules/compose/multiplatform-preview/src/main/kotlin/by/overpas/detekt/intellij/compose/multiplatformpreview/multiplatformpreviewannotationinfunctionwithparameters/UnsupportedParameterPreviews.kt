package by.overpas.detekt.intellij.compose.multiplatformpreview.multiplatformpreviewannotationinfunctionwithparameters

import by.overpas.detekt.intellij.compose.multiplatformpreview.previewAnnotationEntries
import by.overpas.detekt.intellij.compose.multiplatformpreview.previewParameterAnnotation
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.unsupportedParameterPreviews(): List<KtAnnotationEntry> =
    if (valueParameters.all { it.hasDefaultValue() || it.previewParameterAnnotation() != null }) {
        emptyList()
    } else {
        previewAnnotationEntries()
    }
