package by.overpas.detekt.intellij.android.glancepreview.glancepreviewdimensionrespectslimit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("GlancePreviewDimensionRespectsLimit")
class GlancePreviewDimensionRespectsLimit(config: Config) :
    Rule(
        config,
        "A Glance `@Preview` annotation has a `widthDp` or `heightDp` value that is not between 1 and 3000. " +
            "Glance Preview ignores such a value. Set a value between 1 and 3000.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        checkDimensions(function)
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (klass.isAnnotation()) checkDimensions(klass)
    }

    private fun checkDimensions(declaration: KtAnnotated) =
        declaration.annotationEntries
            .mapNotNull { annotation -> analyze(annotation) { annotation.dimensionOutOfLimits() } }
            .forEach { argument ->
                report(
                    Finding(
                        Entity.from(argument),
                        "Preview width and height are limited to be between 1 and 3,000, setting a lower or higher " +
                            "number will not change the preview dimension",
                    ),
                )
            }
}
