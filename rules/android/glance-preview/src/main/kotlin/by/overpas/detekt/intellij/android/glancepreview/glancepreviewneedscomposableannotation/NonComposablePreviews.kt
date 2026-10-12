package by.overpas.detekt.intellij.android.glancepreview.glancepreviewneedscomposableannotation

import by.overpas.detekt.intellij.android.glancepreview.COMPOSABLE
import by.overpas.detekt.intellij.android.glancepreview.previewAnnotationEntries
import by.overpas.detekt.intellij.android.glancepreview.resolvedClassId
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.nonComposablePreviews(): List<KtAnnotationEntry> =
    if (annotationEntries.any { entry -> entry.resolvedClassId() == COMPOSABLE }) {
        emptyList()
    } else {
        previewAnnotationEntries()
    }
