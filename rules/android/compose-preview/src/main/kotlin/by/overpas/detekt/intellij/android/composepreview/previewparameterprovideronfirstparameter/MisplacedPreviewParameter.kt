package by.overpas.detekt.intellij.android.composepreview.previewparameterprovideronfirstparameter

import by.overpas.detekt.intellij.android.composepreview.isPreviewOrMultiPreview
import by.overpas.detekt.intellij.android.composepreview.previewParameterAnnotation
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.misplacedPreviewParameter(): KtAnnotationEntry? =
    when {
        valueParameters.firstOrNull()?.previewParameterAnnotation() != null -> null
        annotationEntries.none { it.isPreviewOrMultiPreview() } -> null
        else -> valueParameters.firstNotNullOfOrNull { it.previewParameterAnnotation() }
    }
