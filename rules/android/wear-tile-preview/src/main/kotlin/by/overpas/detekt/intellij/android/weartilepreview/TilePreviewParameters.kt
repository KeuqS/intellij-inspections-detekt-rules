package by.overpas.detekt.intellij.android.weartilepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.hasTilePreviewParameters(): Boolean =
    valueParameters.isEmpty() || valueParameters.singleOrNull()?.hasContextType() == true
