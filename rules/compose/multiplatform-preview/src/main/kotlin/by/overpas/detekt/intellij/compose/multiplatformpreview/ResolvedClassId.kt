package by.overpas.detekt.intellij.compose.multiplatformpreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotationEntry

context(session: KaSession)
internal fun KtAnnotationEntry.resolvedClassId(): ClassId? =
    with(session) { typeReference?.type?.expandedSymbol?.classId }
