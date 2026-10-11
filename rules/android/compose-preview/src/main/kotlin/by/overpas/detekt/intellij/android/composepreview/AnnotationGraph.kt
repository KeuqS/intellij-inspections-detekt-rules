package by.overpas.detekt.intellij.android.composepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId

context(session: KaSession)
internal fun ClassId.annotationGraph(): Set<ClassId> =
    annotationGraph(emptySet())

context(session: KaSession)
private fun ClassId.annotationGraph(visited: Set<ClassId>): Set<ClassId> {
    if (this in visited || !couldBeMultiPreview()) return visited + this
    val annotations = with(session) { findClass(this@annotationGraph)?.annotations }.orEmpty()
    return annotations.mapNotNull { it.classId }
        .fold(visited + this) { graph, classId -> classId.annotationGraph(graph) }
}
