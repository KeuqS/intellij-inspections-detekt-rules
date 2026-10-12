package by.overpas.detekt.intellij.android.glancepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId

private val NON_MULTIPREVIEW_PREFIXES = listOf("android.", "kotlin.", "kotlinx.", "java.")

context(session: KaSession)
internal fun ClassId.annotationGraph(): Set<ClassId> =
    annotationGraph(emptySet())

context(session: KaSession)
private fun ClassId.annotationGraph(visited: Set<ClassId>): Set<ClassId> {
    val name = asFqNameString()
    val isTraversed = if (name.startsWith("androidx.")) {
        ".preview." in name
    } else {
        NON_MULTIPREVIEW_PREFIXES.none { name.startsWith(it) }
    }
    if (this in visited || !isTraversed) return visited + this
    val annotations = with(session) { findClass(this@annotationGraph)?.annotations }.orEmpty()
    return annotations.mapNotNull { it.classId }
        .fold(visited + this) { graph, classId -> classId.annotationGraph(graph) }
}
