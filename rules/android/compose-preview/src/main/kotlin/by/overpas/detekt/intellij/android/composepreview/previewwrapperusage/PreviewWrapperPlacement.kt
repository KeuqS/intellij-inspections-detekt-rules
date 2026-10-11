package by.overpas.detekt.intellij.android.composepreview.previewwrapperusage

import by.overpas.detekt.intellij.android.composepreview.COMPOSABLE
import by.overpas.detekt.intellij.android.composepreview.PREVIEW_WRAPPER
import by.overpas.detekt.intellij.android.composepreview.isPreviewOrMultiPreview
import by.overpas.detekt.intellij.android.composepreview.resolvedClassId
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

context(session: KaSession)
internal fun KtAnnotationEntry.isMisplacedPreviewWrapper(): Boolean {
    val annotations = getStrictParentOfType<KtNamedFunction>()?.annotationEntries ?: return false
    return isPreviewWrapper() &&
        !(annotations.any { it.resolvedClassId() == COMPOSABLE } && annotations.any { it.isPreviewOrMultiPreview() })
}

context(session: KaSession)
internal fun KtAnnotationEntry.isDuplicatePreviewWrapper(): Boolean {
    val annotationClass = getStrictParentOfType<KtClass>()
        ?.takeIf { it.isAnnotation() && getStrictParentOfType<KtNamedFunction>() == null }
        ?: return false
    return isPreviewWrapper() && annotationClass.annotationEntries.filter { it.isPreviewWrapper() }.indexOf(this) > 0
}

context(session: KaSession)
private fun KtAnnotationEntry.isPreviewWrapper(): Boolean =
    resolvedClassId() == PREVIEW_WRAPPER
