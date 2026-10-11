package by.overpas.detekt.intellij.android.composepreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.android.composepreview.composepreviewdimensionrespectslimit.ComposePreviewDimensionRespectsLimit
import by.overpas.detekt.intellij.android.composepreview.composepreviewneedscomposableannotation.ComposePreviewNeedsComposableAnnotation
import by.overpas.detekt.intellij.android.composepreview.previewapilevelmustbevalid.PreviewApiLevelMustBeValid
import by.overpas.detekt.intellij.android.composepreview.previewdeviceshouldusenewspec.PreviewDeviceShouldUseNewSpec
import by.overpas.detekt.intellij.android.composepreview.previewfontscalemustbegreaterthanzero.PreviewFontScaleMustBeGreaterThanZero
import by.overpas.detekt.intellij.android.composepreview.previewshouldnotbecalledrecursively.PreviewShouldNotBeCalledRecursively
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("inspection.group.name")
class ComposePreviewRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-android-compose-preview")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::ComposePreviewDimensionRespectsLimit,
                ::ComposePreviewNeedsComposableAnnotation,
                ::PreviewApiLevelMustBeValid,
                ::PreviewDeviceShouldUseNewSpec,
                ::PreviewFontScaleMustBeGreaterThanZero,
                ::PreviewShouldNotBeCalledRecursively,
            ),
        )
}
