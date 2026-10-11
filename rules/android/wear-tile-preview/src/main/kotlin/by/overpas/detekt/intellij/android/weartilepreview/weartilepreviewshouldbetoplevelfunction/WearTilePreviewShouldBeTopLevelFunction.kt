package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewshouldbetoplevelfunction

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.weartilepreview.hasTilePreviewAnnotation
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("WearTilePreviewShouldBeTopLevelFunction")
class WearTilePreviewShouldBeTopLevelFunction(config: Config) :
    Rule(
        config,
        "A function with a Tile `@Preview` annotation is declared in another function, in a nested class, or in a " +
            "class without a default constructor. Wear Tile Preview cannot create an instance to call it. Move the " +
            "function to the top level.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isValidTilePreviewLocation()) return
        if (!analyze(function) { function.hasTilePreviewAnnotation() }) return
        report(
            Finding(
                Entity.from(function),
                "The Preview must be a top level declaration or in a top level class with a default constructor",
            ),
        )
    }
}
