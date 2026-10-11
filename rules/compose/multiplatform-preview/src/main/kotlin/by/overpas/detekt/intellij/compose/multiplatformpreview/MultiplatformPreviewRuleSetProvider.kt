package by.overpas.detekt.intellij.compose.multiplatformpreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.compose.multiplatformpreview.multiplatformpreviewannotationinfunctionwithparameters.MultiplatformPreviewAnnotationInFunctionWithParameters
import by.overpas.detekt.intellij.compose.multiplatformpreview.multiplatformpreviewmultipleparameterproviders.MultiplatformPreviewMultipleParameterProviders
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("compose.preview.inspection.group.name")
class MultiplatformPreviewRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-compose-multiplatform-preview")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::MultiplatformPreviewAnnotationInFunctionWithParameters,
                ::MultiplatformPreviewMultipleParameterProviders,
            ),
        )
}
