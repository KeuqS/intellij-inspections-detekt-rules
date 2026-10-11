package by.overpas.detekt.intellij.android.composepreview.previewdeviceshouldusenewspec

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

@IntellijInspection("PreviewDeviceShouldUseNewSpec")
class PreviewDeviceShouldUseNewSpec(config: Config) :
    Rule(
        config,
        "A `@Preview` sets `device` to a legacy device spec that starts with `spec:id`. Compose Preview does not " +
            "support this spec. Use the new device spec, for example `spec:width=411dp,height=891dp`.",
    ),
    RequiresAnalysisApi {

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        analyze(dcl) {
            dcl.directPreviews()
                .mapNotNull { it.namedArgument("device") }
                .filter { argument -> (argument.constantValue() as? String)?.startsWith("spec:id") == true }
        }.forEach { argument ->
            report(
                Finding(
                    Entity.from(argument),
                    "This constant uses a legacy device spec, which is no longer supported",
                ),
            )
        }
    }
}
