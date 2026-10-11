package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcontextcomesfromparameter

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewAnnotation
import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewSignature
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("WearTilePreviewContextComesFromParameter")
class WearTilePreviewContextComesFromParameter(config: Config) :
    Rule(
        config,
        "A Tile preview function uses a `Context` that does not come from its parameter. Wear Tile Preview " +
            "gives the correct `Context` only through the parameter. Use the `Context` parameter of the function.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isLocal) return
        analyze(function) {
            if (function.hasTilePreviewSignature() && function.hasTilePreviewAnnotation()) {
                val contexts = TilePreviewContexts(function)
                contexts.foreignArguments() + contexts.foreignReceivers()
            } else {
                emptyList()
            }
        }.forEach { context ->
            report(Finding(Entity.from(context), "The Context must come from the Preview method parameters"))
        }
    }
}
