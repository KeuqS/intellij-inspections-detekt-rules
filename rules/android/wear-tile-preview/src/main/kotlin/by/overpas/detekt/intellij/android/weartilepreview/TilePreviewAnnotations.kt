package by.overpas.detekt.intellij.android.weartilepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtAnnotationEntry.resolvedClassId(): ClassId? =
    with(session) { typeReference?.type?.expandedSymbol?.classId }

context(session: KaSession)
internal fun KtNamedFunction.hasTilePreviewAnnotation(): Boolean =
    annotationEntries.any { entry -> entry.resolvedClassId()?.let { TILE_PREVIEW in it.annotationGraph() } == true }
