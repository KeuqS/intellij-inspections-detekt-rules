package by.overpas.detekt.intellij.android.glancepreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.android.glancepreview.glancepreviewdimensionrespectslimit.GlancePreviewDimensionRespectsLimit
import by.overpas.detekt.intellij.android.glancepreview.glancepreviewmustbetoplevelfunction.GlancePreviewMustBeTopLevelFunction
import by.overpas.detekt.intellij.android.glancepreview.glancepreviewneedscomposableannotation.GlancePreviewNeedsComposableAnnotation
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("inspection.group.name")
class GlancePreviewRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-android-glance-preview")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::GlancePreviewDimensionRespectsLimit,
                ::GlancePreviewMustBeTopLevelFunction,
                ::GlancePreviewNeedsComposableAnnotation,
            ),
        )
}
