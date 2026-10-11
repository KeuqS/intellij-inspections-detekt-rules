package by.overpas.detekt.intellij.android.glancepreview.glancepreviewneedscomposableannotation

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("GlancePreviewNeedsComposableAnnotation")
class GlancePreviewNeedsComposableAnnotation(config: Config) :
    Rule(
        config,
        "A function with a Glance `@Preview` or a MultiPreview annotation is not `@Composable`. Glance Preview " +
            "renders only composable functions. Add `@Composable` to the function.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        analyze(function) { function.nonComposablePreviews() }.forEach { annotation ->
            report(Finding(Entity.from(annotation), "Glance Preview only works with Composable functions"))
        }
    }
}
