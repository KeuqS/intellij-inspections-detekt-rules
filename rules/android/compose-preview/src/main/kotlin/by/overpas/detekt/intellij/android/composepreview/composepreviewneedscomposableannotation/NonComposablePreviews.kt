package by.overpas.detekt.intellij.android.composepreview.composepreviewneedscomposableannotation

import by.overpas.detekt.intellij.android.composepreview.COMPOSABLE
import by.overpas.detekt.intellij.android.composepreview.previewAnnotationEntries
import by.overpas.detekt.intellij.android.composepreview.resolvedClassId
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
