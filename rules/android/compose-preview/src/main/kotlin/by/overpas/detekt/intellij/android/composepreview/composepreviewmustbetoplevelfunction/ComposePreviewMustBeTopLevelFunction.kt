package by.overpas.detekt.intellij.android.composepreview.composepreviewmustbetoplevelfunction

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.composepreview.previewAnnotationEntries
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("ComposePreviewMustBeTopLevelFunction")
class ComposePreviewMustBeTopLevelFunction(config: Config) :
    Rule(
        config,
        "A function with `@Preview` or a MultiPreview annotation is not a top-level function or a function of a " +
            "top-level class with a default constructor. Compose Preview cannot call this function. Move the " +
            "function to the top level.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.isValidPreviewLocation()) return
        analyze(function) { function.previewAnnotationEntries() }.forEach { annotation ->
            report(
                Finding(
                    Entity.from(annotation),
                    "Preview must be a top level declaration or in a top level class with a default constructor.",
                ),
            )
        }
    }
}
