package by.overpas.detekt.intellij.android.composepreview.composepreviewdimensionrespectslimit

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.composepreview.directPreviews
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtDeclaration

@IntellijInspection("ComposePreviewDimensionRespectsLimit")
class ComposePreviewDimensionRespectsLimit(config: Config) :
    Rule(
        config,
        "A `@Preview` sets `widthDp` or `heightDp` to a value that is not between 1 and 3000. Compose Preview " +
            "ignores such a value. Set a value in this range.",
    ),
    RequiresAnalysisApi {

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        analyze(dcl) { dcl.directPreviews().mapNotNull { it.lastDimensionOutOfLimits() } }.forEach { argument ->
            report(
                Finding(
                    Entity.from(argument),
                    "Preview width and height are limited to be between 1 and 3000, setting a lower or higher " +
                        "number will not change the preview dimension",
                ),
            )
        }
    }
}
