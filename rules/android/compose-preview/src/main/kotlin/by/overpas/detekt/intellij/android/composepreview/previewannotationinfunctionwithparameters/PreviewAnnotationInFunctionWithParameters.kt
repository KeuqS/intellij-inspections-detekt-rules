package by.overpas.detekt.intellij.android.composepreview.previewannotationinfunctionwithparameters

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("PreviewAnnotationInFunctionWithParameters")
class PreviewAnnotationInFunctionWithParameters(config: Config) :
    Rule(
        config,
        "A function with `@Preview` or a MultiPreview annotation has a parameter without a default value and " +
            "without `@PreviewParameter`. Compose Preview cannot supply a value for this parameter. Add a default " +
            "value or `@PreviewParameter` to the parameter.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        analyze(function) { function.unsupportedParameterPreviews() }.forEach { annotation ->
            report(
                Finding(
                    Entity.from(annotation),
                    "Composable functions with non-default parameters are not supported in Preview unless they " +
                        "are annotated with @PreviewParameter",
                ),
            )
        }
    }
}
