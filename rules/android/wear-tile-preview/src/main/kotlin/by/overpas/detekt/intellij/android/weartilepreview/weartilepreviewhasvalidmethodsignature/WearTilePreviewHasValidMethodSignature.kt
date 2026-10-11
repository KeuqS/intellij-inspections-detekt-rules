package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewhasvalidmethodsignature

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewAnnotation
import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewParameters
import by.overpas.detekt.intellij.android.weartilepreview.returnsTilePreviewData
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("WearTilePreviewHasValidMethodSignature")
class WearTilePreviewHasValidMethodSignature(config: Config) :
    Rule(
        config,
        "A function with a Tile `@Preview` annotation does not return `TilePreviewData`, or has parameters " +
            "other than one `Context`. Wear Tile Preview cannot render such a function. Return `TilePreviewData` " +
            "and use no parameters or one `Context` parameter.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isLocal) return
        val (returnsTilePreviewData, hasTilePreviewParameters) = analyze(function) {
            if (function.hasTilePreviewAnnotation()) {
                function.returnsTilePreviewData() to function.hasTilePreviewParameters()
            } else {
                null
            }
        } ?: return
        if (!returnsTilePreviewData) {
            report(
                Finding(
                    Entity.atName(function),
                    "The Preview method should return androidx.wear.tiles.tooling.preview.TilePreviewData",
                ),
            )
        }
        val parameterList = function.valueParameterList
        if (!hasTilePreviewParameters && parameterList != null) {
            report(
                Finding(
                    Entity.from(parameterList),
                    "The Preview method should only have a single optional android.content.Context parameter",
                ),
            )
        }
    }
}
