package by.overpas.detekt.intellij.android.weartilepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter

context(session: KaSession)
internal fun KtNamedFunction.hasTilePreviewSignature(): Boolean =
    returnsTilePreviewData() && hasTilePreviewParameters()

context(session: KaSession)
internal fun KtNamedFunction.returnsTilePreviewData(): Boolean =
    with(session) { (symbol.returnType as? KaClassType)?.classId == TILE_PREVIEW_DATA }

context(session: KaSession)
internal fun KtNamedFunction.hasTilePreviewParameters(): Boolean =
    valueParameters.isEmpty() || valueParameters.singleOrNull()?.hasContextType() == true

context(session: KaSession)
internal fun KtParameter.hasContextType(): Boolean =
    with(session) { (typeReference?.type as? KaClassType)?.classId == CONTEXT }
