package by.overpas.detekt.intellij.android.composepreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.android.composepreview.composepreviewmustbetoplevelfunction.ComposePreviewMustBeTopLevelFunction
import by.overpas.detekt.intellij.android.composepreview.composepreviewneedscomposableannotation.ComposePreviewNeedsComposableAnnotation
import by.overpas.detekt.intellij.android.composepreview.previewannotationinfunctionwithparameters.PreviewAnnotationInFunctionWithParameters
import by.overpas.detekt.intellij.android.composepreview.previewmultipleparameterproviders.PreviewMultipleParameterProviders
import by.overpas.detekt.intellij.android.composepreview.previewparameterprovideronfirstparameter.PreviewParameterProviderOnFirstParameter
import by.overpas.detekt.intellij.android.composepreview.previewwrapperusage.PreviewWrapperUsage
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
                ::ComposePreviewMustBeTopLevelFunction,
                ::ComposePreviewNeedsComposableAnnotation,
                ::PreviewAnnotationInFunctionWithParameters,
                ::PreviewMultipleParameterProviders,
                ::PreviewParameterProviderOnFirstParameter,
                ::PreviewWrapperUsage,
            ),
        )
}
