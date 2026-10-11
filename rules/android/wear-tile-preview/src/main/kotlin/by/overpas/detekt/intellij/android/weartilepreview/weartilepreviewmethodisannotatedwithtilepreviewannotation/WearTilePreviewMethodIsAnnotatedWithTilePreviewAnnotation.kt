package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewmethodisannotatedwithtilepreviewannotation

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewSignature
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("WearTilePreviewMethodIsAnnotatedWithTilePreviewAnnotation")
class WearTilePreviewMethodIsAnnotatedWithTilePreviewAnnotation(config: Config) :
    Rule(
        config,
        "A function with the Tile preview signature has a `Preview` annotation from another package, or a " +
            "MultiPreview annotation that refers to one. Wear Tile Preview shows only Tile `@Preview` annotations. " +
            "Use `androidx.wear.tiles.tooling.preview.Preview`.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isLocal) return
        analyze(function) {
            if (function.hasTilePreviewSignature()) {
                function.annotationEntries.mapNotNull { annotation ->
                    annotation.foreignPreview()?.let { annotation to it }
                }
            } else {
                emptyList()
            }
        }.forEach { (annotation, foreignPreview) ->
            report(Finding(Entity.from(annotation), foreignPreview.message))
        }
    }
}
