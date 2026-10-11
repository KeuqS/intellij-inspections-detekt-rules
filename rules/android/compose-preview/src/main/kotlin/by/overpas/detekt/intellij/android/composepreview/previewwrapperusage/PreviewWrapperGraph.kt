package by.overpas.detekt.intellij.android.composepreview.previewwrapperusage

import by.overpas.detekt.intellij.android.composepreview.PREVIEW_WRAPPER
import by.overpas.detekt.intellij.android.composepreview.couldBeMultiPreview
import by.overpas.detekt.intellij.android.composepreview.resolvedClassId
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotated

private data class WrapperSearch(
    val expanded: Set<ClassId> = emptySet(),
    val wrappers: Int = 0,
)

context(session: KaSession)
internal fun KtAnnotated.previewWrappersInGraph(): Int =
    annotationEntries.mapNotNull { it.resolvedClassId() }
        .fold(WrapperSearch()) { search, classId -> classId.visitedBy(search) }
        .wrappers

context(session: KaSession)
private fun ClassId.visitedBy(search: WrapperSearch): WrapperSearch =
    when {
        this == PREVIEW_WRAPPER -> search.copy(wrappers = search.wrappers + 1)

        this in search.expanded || !couldBeMultiPreview() -> search

        else ->
            with(session) { findClass(this@visitedBy)?.annotations }.orEmpty()
                .mapNotNull { it.classId }
                .fold(search.copy(expanded = search.expanded + this)) { graph, child -> child.visitedBy(graph) }
    }
