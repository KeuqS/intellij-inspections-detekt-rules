package by.overpas.detekt.intellij.android.weartilepreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcomposableannotationisnotsupported.WearTilePreviewComposableAnnotationIsNotSupported
import by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcontextcomesfromparameter.WearTilePreviewContextComesFromParameter
import by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewhasvalidmethodsignature.WearTilePreviewHasValidMethodSignature
import by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewmethodisannotatedwithtilepreviewannotation.WearTilePreviewMethodIsAnnotatedWithTilePreviewAnnotation
import by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewshouldbetoplevelfunction.WearTilePreviewShouldBeTopLevelFunction
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("inspection.group.name")
class WearTilePreviewRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-android-wear-tile-preview")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::WearTilePreviewComposableAnnotationIsNotSupported,
                ::WearTilePreviewContextComesFromParameter,
                ::WearTilePreviewHasValidMethodSignature,
                ::WearTilePreviewMethodIsAnnotatedWithTilePreviewAnnotation,
                ::WearTilePreviewShouldBeTopLevelFunction,
            ),
        )
}
