package by.overpas.detekt.intellij.android.glancepreview.glancepreviewmustbetoplevelfunction

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.glancepreview.previewAnnotationEntries
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("GlancePreviewMustBeTopLevelFunction")
class GlancePreviewMustBeTopLevelFunction(config: Config) :
    Rule(
        config,
        "A function with a Glance `@Preview` or a MultiPreview annotation is not a top level function, and not a " +
            "function of a top level class with a default constructor. Glance Preview cannot create an instance to " +
            "call it. Move the function to the top level.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isValidPreviewLocation()) return
        analyze(function) { function.previewAnnotationEntries() }.forEach { annotation ->
            report(
                Finding(
                    Entity.from(annotation),
                    "Glance Preview must be a top level declaration or in a top level class with a default " +
                        "constructor.",
                ),
            )
        }
    }
}
