package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewmethodisannotatedwithtilepreviewannotation

import by.overpas.detekt.intellij.android.weartilepreview.TILE_PREVIEW
import by.overpas.detekt.intellij.android.weartilepreview.annotationGraph
import by.overpas.detekt.intellij.android.weartilepreview.resolvedClassId
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotationEntry

context(session: KaSession)
internal fun KtAnnotationEntry.foreignPreview(): ForeignPreview? {
    val classId = resolvedClassId() ?: return null
    return when {
        classId.isForeignPreview() -> ForeignPreview.DIRECT
        classId.annotationGraph().any { it.isForeignPreview() } -> ForeignPreview.MULTI_PREVIEW
        else -> null
    }
}

private fun ClassId.isForeignPreview(): Boolean =
    asFqNameString().endsWith(".Preview") && this != TILE_PREVIEW
