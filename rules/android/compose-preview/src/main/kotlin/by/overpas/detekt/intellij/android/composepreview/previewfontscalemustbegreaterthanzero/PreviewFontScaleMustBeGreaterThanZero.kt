package by.overpas.detekt.intellij.android.composepreview.previewfontscalemustbegreaterthanzero

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.composepreview.constantValue
import by.overpas.detekt.intellij.android.composepreview.directPreviews
import by.overpas.detekt.intellij.android.composepreview.namedArgument
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtDeclaration

@IntellijInspection("PreviewFontScaleMustBeGreaterThanZero")
class PreviewFontScaleMustBeGreaterThanZero(config: Config) :
    Rule(
        config,
        "A `@Preview` sets `fontScale` to zero or a negative value. Compose Preview cannot render text with " +
            "such a scale. Set a value that is greater than zero.",
    ),
    RequiresAnalysisApi {

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        analyze(dcl) {
            dcl.directPreviews()
                .mapNotNull { it.namedArgument("fontScale") }
                .filter { argument -> (argument.constantValue() as? Float)?.let { it <= 0f } == true }
        }.forEach { argument ->
            report(Finding(Entity.from(argument), "Preview fontScale value must be greater than zero"))
        }
    }
}
