package by.overpas.detekt.intellij.android.composepreview.previewparameterprovideronfirstparameter

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("PreviewParameterProviderOnFirstParameter")
class PreviewParameterProviderOnFirstParameter(config: Config) :
    Rule(
        config,
        "A function with `@Preview` or a MultiPreview annotation has `@PreviewParameter` on a parameter that is " +
            "not the first. Compose Preview supports `@PreviewParameter` only on the first parameter. Move this " +
            "parameter to the first position.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        analyze(function) { function.misplacedPreviewParameter() }?.let { annotation ->
            report(
                Finding(
                    Entity.from(annotation),
                    "@PreviewParameter is only allowed in the first parameter of a Composable function",
                ),
            )
        }
    }
}
