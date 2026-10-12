package by.overpas.detekt.intellij.android.composepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtParameter

context(session: KaSession)
internal fun KtParameter.previewParameterAnnotation(): KtAnnotationEntry? =
    annotationEntries.firstOrNull { it.resolvedClassId() == PREVIEW_PARAMETER }
