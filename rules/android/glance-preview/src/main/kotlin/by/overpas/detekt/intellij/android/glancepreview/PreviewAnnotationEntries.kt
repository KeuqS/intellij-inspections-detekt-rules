package by.overpas.detekt.intellij.android.glancepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry

context(session: KaSession)
internal fun KtAnnotationEntry.resolvedClassId(): ClassId? =
    with(session) { typeReference?.type?.expandedSymbol?.classId }

context(session: KaSession)
internal fun KtAnnotationEntry.isPreview(): Boolean =
    resolvedClassId() == PREVIEW

context(session: KaSession)
internal fun KtAnnotationEntry.isPreviewOrMultiPreview(): Boolean =
    resolvedClassId()?.let { PREVIEW in it.annotationGraph() } == true

context(session: KaSession)
internal fun KtAnnotated.previewAnnotationEntries(): List<KtAnnotationEntry> =
    annotationEntries.filter { it.isPreviewOrMultiPreview() }
