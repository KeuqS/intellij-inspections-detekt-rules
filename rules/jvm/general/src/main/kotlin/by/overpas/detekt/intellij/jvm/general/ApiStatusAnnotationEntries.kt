package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry

context(session: KaSession)
internal fun KtAnnotated.findApiStatusEntry(vararg classIds: ClassId): KtAnnotationEntry? =
    annotationEntries.firstOrNull { it.apiStatusClassId() in classIds }
