package by.overpas.detekt.intellij.android.weartilepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.hasTilePreviewSignature(): Boolean =
    returnsTilePreviewData() && hasTilePreviewParameters()
