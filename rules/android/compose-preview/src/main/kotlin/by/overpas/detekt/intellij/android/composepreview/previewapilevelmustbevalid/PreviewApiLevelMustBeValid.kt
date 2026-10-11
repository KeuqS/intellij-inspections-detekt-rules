package by.overpas.detekt.intellij.android.composepreview.previewapilevelmustbevalid

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.composepreview.constantValue
import by.overpas.detekt.intellij.android.composepreview.directPreviews
import by.overpas.detekt.intellij.android.composepreview.namedArgument
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtDeclaration

@IntellijInspection("PreviewApiLevelMustBeValid")
class PreviewApiLevelMustBeValid(config: Config) :
    Rule(
        config,
        "A `@Preview` sets `apiLevel` to an API level that Compose Preview does not support. Compose Preview " +
            "cannot render the preview. Set a supported API level.",
    ),
    RequiresAnalysisApi {

    @Configuration("The lowest API level that Compose Preview supports.")
    private val minApiLevel: Int by config(22)

    @Configuration("The highest API level that Compose Preview supports.")
    private val maxApiLevel: Int by config(37)

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        analyze(dcl) {
            dcl.directPreviews()
                .mapNotNull { it.namedArgument("apiLevel") }
                .filter { argument ->
                    (argument.constantValue() as? Int)?.let { it !in minApiLevel..maxApiLevel } ==
                        true
                }
        }.forEach { argument ->
            report(
                Finding(
                    Entity.from(argument),
                    "Preview apiLevel must be set to an integer between $minApiLevel and $maxApiLevel",
                ),
            )
        }
    }
}
