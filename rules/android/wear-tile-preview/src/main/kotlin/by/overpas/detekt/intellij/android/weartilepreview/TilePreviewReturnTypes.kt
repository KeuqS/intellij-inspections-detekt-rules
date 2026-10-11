package by.overpas.detekt.intellij.android.weartilepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.returnsTilePreviewData(): Boolean =
    with(session) { (symbol.returnType as? KaClassType)?.classId == TILE_PREVIEW_DATA }
