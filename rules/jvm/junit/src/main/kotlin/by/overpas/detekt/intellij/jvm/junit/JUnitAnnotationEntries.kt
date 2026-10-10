package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry

context(session: KaSession)
internal fun KtAnnotationEntry.resolvedClassId(): ClassId? =
    with(session) { typeReference?.type?.expandedSymbol?.classId }

context(session: KaSession)
internal fun KtAnnotated.annotationEntriesOf(vararg classIds: ClassId): List<KtAnnotationEntry> =
    annotationEntries.filter { it.resolvedClassId() in classIds }

context(session: KaSession)
internal fun KtAnnotated.hasAnnotationEntry(vararg classIds: ClassId): Boolean = annotationEntriesOf(*classIds).isNotEmpty()
