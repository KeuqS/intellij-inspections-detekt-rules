package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcomposableannotationisnotsupported

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("WearTilePreviewComposableAnnotationIsNotSupported")
class WearTilePreviewComposableAnnotationIsNotSupported(config: Config) :
    Rule(
        config,
        "A Tile preview function is `@Composable`. Wear Tile Preview does not support composable functions. " +
            "Remove `@Composable` from the function.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isLocal) return
        val annotation = analyze(function) { function.composableOnTilePreview() } ?: return
        report(Finding(Entity.from(annotation), "The @Composable annotation is not supported on Tile previews"))
    }
}
